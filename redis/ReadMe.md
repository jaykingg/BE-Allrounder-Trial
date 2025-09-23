## Redis 모듈: 캐싱 + 분산락 (실무 예제)

Spring Data Redis 기반의 캐시와 간단한 분산락 구현(AOP), 도메인(`Product`)과 API를 포함

### 주요 기능

- 캐싱: `@Cacheable`, `@CachePut`, `@CacheEvict`와 `RedisCacheManager`
- 분산락: `@DistributedLock(key = "product:#{#productId}")` AOP로 락 획득/해제
- 예제 API: 상품 저장/조회/삭제, 재고 감소(분산락)

### 개념과 원리

#### 캐싱(Cache)

- **개념**: 자주 조회되는 데이터를 더 빠른 저장소(메모리/Redis)에 보관해 DB/원본 조회를 줄여 응답속도와 처리량을 높입니다.
- **원리**: 키-값으로 저장. 스프링에서는 `@Cacheable`이 메서드의 파라미터로 키를 생성하고, 결과를 캐시에 저장합니다. 다음 호출부터는 캐시에서 즉시 반환합니다. `@CachePut`는 실행 결과를
  항상 캐시에 갱신하고, `@CacheEvict`는 캐시를 비웁니다.
- **TTL(Time To Live)**: 캐시 유효시간이 지나면 항목이 만료되어 자동 제거됩니다. 본 예제는 기본 10분(`application.yml`) 입니다.
- **주의점**: 캐시와 원본 데이터 간의 일관성. 변경 시 `CachePut/Evict`로 동기화하거나, 짧은 TTL/캐시 무효화 전략을 사용합니다.

#### 분산락(Distributed Lock)

- **개념**: 여러 서버/스레드가 같은 리소스를 동시에 수정하지 않도록 전역적으로 한 번에 하나만 접근하도록 보장하는 메커니즘입니다.
- **원리(SET NX + EX)**: Redis의 `SET key value NX PX <ttl>`를 사용해 키가 없을 때만 키를 생성합니다. 성공하면 락을 획득한 것이고, TTL을 설정하여 장애로 해제 못해도
  자동 만료되게 합니다. 해제는 "내가 설정한 value(토큰)일 때만 DEL" 하는 Lua 스크립트로 안전하게 수행합니다.
- **본 예제 구현**: `RedisLockManager`가 `setIfAbsent`로 락 획득, Lua 스크립트로 토큰 일치 시에만 해제. `DistributedLockAspect`가 AOP로 메서드 진입 전
  락을 획득하고 종료 시 해제합니다. 키는 SpEL(`product:#{#productId}`)로 계산합니다.
- **주의점**: 긴 작업은 `leaseMillis`(TTL)를 넉넉하게, 재시도 대기(`waitMillis`)를 설정. 멱등 설계, 타임아웃/실패 로깅, 모니터링 권장.

### 실행 전제

- Redis 서버가 로컬 `localhost:6379` 에서 실행 중이어야 합니다.
- 포트: 애플리케이션 8087 (`src/main/resources/application.yml` 참고)

### 의존성 요약

- `spring-boot-starter-data-redis`, `spring-boot-starter-cache`, `spring-boot-starter-aop`
- 테스트: `testcontainers:redis`

### 구성 요약

- `RedisConfig`: `RedisTemplate`, `RedisCacheManager`, 커스텀 `KeyGenerator`
- `DistributedLock` + `DistributedLockAspect` + `RedisLockManager`
- `ProductService`는 캐싱/분산락을 사용하는 서비스 예제

### API

- GET `/api/products/{id}`: 상품 조회(캐시 사용)
- POST `/api/products`: 상품 저장/수정 후 캐시 갱신
    - Body 예시
  ```json
  {"id":1, "name":"Apple", "stock":100}
  ```
- DELETE `/api/products/{id}`: 상품 삭제 후 캐시 제거
- POST `/api/products/{id}/decrease?quantity=10`: 재고 감소(분산락)

### 분산락 사용법

서비스 메서드에 아래와 같이 어노테이션만 선언하면 됩니다.

```kotlin
@DistributedLock(key = "lock:product:#{#productId}", waitMillis = 2_000, leaseMillis = 5_000)
fun decreaseStock(productId: Long, quantity: Int): Product {
    ...
}
```

- `key`: 락 키. SpEL로 메서드 파라미터 값을 키에 포함 가능
- `waitMillis`: 락 대기 최대시간. 실패 시 예외 발생
- `leaseMillis`: 락 자동 만료시간. 긴 작업은 적절히 늘리세요

### 캐시 사용법

- 조회: `@Cacheable(cacheNames=["product"], key="#id")`
- 저장/수정: `@CachePut(cacheNames=["product"], key="#result.id")`
- 삭제: `@CacheEvict(cacheNames=["product"], key="#id")`

### 구현 흐름과 시나리오

1) 상품 저장/수정(Upsert)
    - Controller → `ProductService.upsertProduct`
    - Repository가 Redis에 `product:{id}`로 저장
    - `@CachePut`로 동일 키 캐시도 최신화

2) 상품 조회(Get)
    - Controller → `ProductService.getProduct`
    - `@Cacheable`이 먼저 캐시에서 `product::{id}` 조회
    - 캐시에 없으면 Repository 호출 → 결과를 캐시에 저장 후 반환

3) 상품 삭제(Delete)
    - Controller → `ProductService.deleteProduct`
    - Repository에서 Redis 키 삭제
    - `@CacheEvict`로 캐시도 제거

4) 재고 감소(Decrease, 경쟁 상황)
    - Controller → `ProductService.decreaseStock`
    - `@DistributedLock(key="lock:product:#{#productId}")`가 락 획득 시도(SET NX + TTL)
    - 락 획득 성공 시 현재 재고 로드 → 검증 → 감소 → 저장 → 해제
    - 동시 10요청이어도 순차 처리되어 재고가 정확히 감소

### 확장 포인트 (실무 팁)

- 키 전략: 모듈/도메인 접두사 사용(예: `product:1`)
- TTL: 도메인별 캐시 만료를 별도 설정으로 분리 가능
- 락 충돌 로그/메트릭: AOP에서 실패 건 수 집계, 슬랙 알림 연계
- 고급 락: 멀티 노드/고가용 요구 시 Redisson 도입 고려

### 분산락 장애/타임아웃 리트라이 정책 예시

간단한 고정 백오프 예시(AOP 내부 로직 확장 또는 호출부 재시도):

```kotlin
fun <T> withRetry(maxAttempts: Int = 3, backoffMs: Long = 100, block: () -> T): T {
    var last: Throwable? = null
    repeat(maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: IllegalStateException) { // 락 획득 실패 등
            last = e
            Thread.sleep(backoffMs * (attempt + 1)) // 선형 백오프
        }
    }
    throw last ?: IllegalStateException("retry failed")
}

// 사용 예: 락 경합 높은 구간에 적용
withRetry { productService.decreaseStock(id, qty) }
```

- 고급: 지수 백오프 + 재시도 Jitter(무작위 지연)로 동시 재충돌을 줄입니다.
- 주의: 비즈니스 멱등성 보장(중복 수행 시 동일 결과). 실패 로깅/모니터링 필수.

### Redisson 공정 락/멀티락 비교(개요)

- 공정 락(Fair Lock): 대기 큐 순서대로 락을 부여해 starvation을 방지. 지연은 늘 수 있음.
- 표준 락(Non-fair): 처리량 유리하나 일부 요청이 계속 뒤로 밀릴 수 있음.
- 멀티락(Redisson MultiLock): 여러 키(또는 여러 Redis 노드)에 동시에 락을 걸어 합의적으로 보호. 고가용/샤딩 환경에서 유용.

본 예제는 경량 구현(SET NX)으로 충분한 경우를 다루며, 다음처럼 확장 가능합니다:

```kotlin
// build.gradle.kts
implementation("org.redisson:redisson-spring-boot-starter:3.27.2")

// 사용 예
@Service
class OrderService(private val redissonClient: org.redisson.api.RedissonClient) {
    fun placeOrder(userId: Long, productId: Long) {
        val rLock = redissonClient.getFairLock("lock:product:$productId") // 공정 락
        rLock.lock()
        try {
            // critical section
        } finally {
            rLock.unlock()
        }
    }
}
```

멀티락은 `redissonClient.getMultiLock(lock1, lock2, ...)` 사용. 운영 환경에서는 연결/타임아웃, 공정 락의 대기 큐 길이 모니터링을 권장합니다.

### 캐시/분산락 상태 확인 방법(docker로 Redis 실행 가정)

1) 컨테이너 접근

```bash
docker exec -it <redis-container> redis-cli
```

2) 키 조회/패턴 확인

```bash
KEYS product:*           # 캐시된 상품(주의: 운영에서는 SCAN 사용 권장)
KEYS lock:product:*      # 분산락 키
TTL product:1            # 캐시 남은 TTL
GET lock:product:1       # 락 토큰(해제 시 토큰 일치 확인)
```

3) 안전한 운영 명령

```bash
SCAN 0 MATCH product:* COUNT 100
```

4) 실시간 모니터링(간단)

```bash
MONITOR | grep -E "SET|DEL|EXPIRE|EVAL"
```

5) 애플리케이션 레벨

- 로그 레벨을 `org.springframework.data.redis=DEBUG`로 두면 캐시/락 명령 흐름을 확인 가능
- 메트릭: Actuator + Micrometer로 Redis 통계/락 실패 카운트 노출 권장

### 테스트(개요)

- Testcontainers Redis로 통합 테스트 가능
- 동시 요청에 대해 `decreaseStock`이 일관된 결과를 내는지 검증

### 디렉터리

- `config/RedisConfig.kt`: 캐시/RedisTemplate 구성
- `lock/*`: 분산락 어노테이션/AOP/매니저
- `domain/Product.kt`, `repository/ProductRedisRepository.kt`
- `application/ProductService.kt`, `controller/ProductController.kt`


