# Redis 모듈: 간단한 캐시 + 분산락 예제

Redis 캐시와 분산락을 구현

## 🎯 주요 특징

- **간단함**: 복잡한 설정 없이 6개 파일로 완성
- **실용적**: 실제 프로젝트에서 바로 사용 가능
- **확장성**: 다른 도메인 쉽게 추가 가능

## 📁 프로젝트 구조

```
redis/
├── domain/
│   └── Product.kt                    # 도메인 클래스
├── config/
│   └── RedisConfig.kt               # Redis 설정
├── lock/
│   ├── DistributedLock.kt           # 분산락 어노테이션
│   └── DistributedLockAspect.kt     # 분산락 AOP
├── service/
│   └── ProductService.kt            # 비즈니스 로직
└── controller/
    └── ProductController.kt         # REST API
```

## 🚀 핵심 기능

### 1. Redis 캐시

- `@Cacheable`: 조회 시 캐시에서 먼저 확인
- `@CachePut`: 저장/수정 시 캐시 업데이트
- `@CacheEvict`: 삭제 시 캐시 제거

### 2. 분산락

- `@DistributedLock`: 동시성 제어를 위한 분산락
- AOP로 자동 락 획득/해제
- 재고 감소 시 동시 접근 방지

## 🛠️ 실행 전제

- Redis 서버가 `localhost:6379`에서 실행 중
- 애플리케이션 포트: `8087`

## 📋 API 엔드포인트

### 상품 관리

```bash
# 모든 상품 조회
GET /api/products

# 상품 조회 (캐시 사용)
GET /api/products/{id}

# 상품 생성
POST /api/products
Content-Type: application/json
{
  "name": "테스트 상품",
  "stock": 100
}

# 상품 수정
PUT /api/products/{id}
Content-Type: application/json
{
  "name": "수정된 상품",
  "stock": 50
}

# 상품 삭제
DELETE /api/products/{id}
```

### 재고 관리 (분산락 사용)

```bash
# 재고 감소 (동시성 제어)
POST /api/products/{id}/decrease-stock?quantity=10
```

## 💡 사용법

### 캐시 사용법

```kotlin
@Service
class ProductService {

    // 조회 시 캐시 사용
    @Cacheable(cacheNames = ["product"], key = "#id", unless = "#result == null")
    fun getProduct(id: Long): Product? {
        return products[id]
    }

    // 저장 시 캐시 업데이트
    @CachePut(cacheNames = ["product"], key = "#result.id", unless = "#result == null")
    fun saveProduct(product: Product): Product {
        // 저장 로직
    }

    // 삭제 시 캐시 제거
    @CacheEvict(cacheNames = ["product"], key = "#id")
    fun deleteProduct(id: Long) {
        // 삭제 로직
    }
}
```

### 분산락 사용법

```kotlin
@Service
class ProductService {

    // 분산락으로 동시성 제어
    @DistributedLock(key = "lock:product:#{#productId}", waitMillis = 1000, leaseMillis = 5000)
    fun decreaseStock(productId: Long, quantity: Int): Product {
        // 재고 감소 로직 (동시 접근 방지)
    }
}
```

## 🔧 설정

### application.yml

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10분
      cache-null-values: false
```

### Redis 설정

```kotlin
@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.setConnectionFactory(connectionFactory)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.afterPropertiesSet()
        return template
    }
}
```

## 🧪 테스트 방법

### 1. 상품 생성 및 조회

```bash
# 상품 생성
curl -X POST http://localhost:8087/api/products \
  -H "Content-Type: application/json" \
  -d '{"name": "테스트 상품", "stock": 100}'

# 상품 조회 (캐시 확인)
curl http://localhost:8087/api/products/1
```

### 2. 분산락 테스트

```bash
# 재고 감소 (동시 요청으로 테스트)
curl -X POST "http://localhost:8087/api/products/1/decrease-stock?quantity=10"
```

### 3. Redis 데이터 확인

```bash
# Redis CLI 접속
redis-cli

# 캐시된 데이터 확인
KEYS product:*
GET product::1

# 분산락 확인
KEYS lock:*
GET lock:product:1
```

## 🔄 동작 흐름

### 캐시 흐름

1. **조회**: 캐시 확인 → 없으면 메모리에서 조회 → 캐시에 저장 → 반환
2. **저장**: 메모리에 저장 → 캐시 업데이트
3. **삭제**: 메모리에서 삭제 → 캐시 제거

### 분산락 흐름

1. **락 획득**: Redis에 `SET key value NX PX ttl` 실행
2. **비즈니스 로직**: 락 획득 성공 시에만 실행
3. **락 해제**: Lua 스크립트로 안전하게 해제

## 🎯 확장 방법

### 다른 도메인 추가 (예: User)

1. `User.kt` 도메인 클래스 생성
2. `UserService.kt`에서 `@DistributedLock` 사용
3. `UserController.kt` 생성

```kotlin
// User.kt
data class User(
    val id: Long,
    val name: String,
    val email: String
)

// UserService.kt
@Service
class UserService {
    @DistributedLock(key = "lock:user:#{#userId}")
    fun updateUser(userId: Long, user: User): User {
        // 사용자 수정 로직
    }
}
```

## 📊 모니터링

### Redis 명령어

```bash
# 실시간 모니터링
MONITOR

# 키 패턴 확인
SCAN 0 MATCH product:* COUNT 100
SCAN 0 MATCH lock:* COUNT 100

# TTL 확인
TTL product::1
```

### 로그 확인

```yaml
logging:
  level:
    rounderall.redis: DEBUG
```

## 🔄 분산락 고급 기능

### 리트라이 정책

분산락 획득 실패 시 재시도하는 정책을 구현할 수 있습니다:

```kotlin
@Service
class ProductService {

    // 재시도 로직이 포함된 분산락
    @DistributedLock(key = "lock:product:#{#productId}", waitMillis = 2000, leaseMillis = 5000)
    fun decreaseStockWithRetry(productId: Long, quantity: Int): Product {
        return withRetry(maxAttempts = 3, backoffMs = 100) {
            decreaseStockInternal(productId, quantity)
        }
    }

    private fun <T> withRetry(maxAttempts: Int = 3, backoffMs: Long = 100, block: () -> T): T {
        var last: Throwable? = null
        repeat(maxAttempts) { attempt ->
            try {
                return block()
            } catch (e: RuntimeException) { // 락 획득 실패 등
                last = e
                if (attempt < maxAttempts - 1) {
                    Thread.sleep(backoffMs * (attempt + 1)) // 선형 백오프
                }
            }
        }
        throw last ?: RuntimeException("재시도 실패")
    }

    private fun decreaseStockInternal(productId: Long, quantity: Int): Product {
        // 실제 재고 감소 로직
        val product = products[productId]
            ?: throw IllegalArgumentException("상품이 존재하지 않습니다: $productId")

        require(quantity > 0) { "감소 수량은 0보다 커야 합니다" }
        require(product.stock >= quantity) { "재고 부족: 현재 ${product.stock}, 요청 ${quantity}" }

        val updatedProduct = product.copy(stock = product.stock - quantity)
        products[productId] = updatedProduct
        return updatedProduct
    }
}
```

### 고급 리트라이 전략

```kotlin
// 지수 백오프 + Jitter (무작위 지연)
private fun <T> withExponentialBackoff(maxAttempts: Int = 5, block: () -> T): T {
    var last: Throwable? = null
    repeat(maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: RuntimeException) {
            last = e
            if (attempt < maxAttempts - 1) {
                val baseDelay = 100L * (1L shl attempt) // 지수 백오프
                val jitter = (Math.random() * 100).toLong() // Jitter 추가
                Thread.sleep(baseDelay + jitter)
            }
        }
    }
    throw last ?: RuntimeException("재시도 실패")
}
```

### 멀티락 (Redisson 사용)

여러 리소스에 동시에 락을 걸어야 하는 경우:

```kotlin
// build.gradle.kts에 추가
implementation("org.redisson:redisson-spring-boot-starter:3.27.2")

// Redisson 설정
@Configuration
class RedissonConfig {
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://localhost:6379")
        return Redisson.create(config)
    }
}

// 멀티락 사용 예제
@Service
class OrderService(
    private val redissonClient: RedissonClient
) {

    fun transferStock(fromProductId: Long, toProductId: Long, quantity: Int) {
        // 여러 상품에 동시에 락을 걸어야 하는 경우
        val lock1 = redissonClient.getLock("lock:product:$fromProductId")
        val lock2 = redissonClient.getLock("lock:product:$toProductId")

        val multiLock = redissonClient.getMultiLock(lock1, lock2)

        try {
            // 모든 락을 획득할 때까지 대기
            val acquired = multiLock.tryLock(5, 10, TimeUnit.SECONDS)
            if (acquired) {
                // 두 상품의 재고를 안전하게 이전
                transferStockInternal(fromProductId, toProductId, quantity)
            } else {
                throw RuntimeException("멀티락 획득 실패")
            }
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock()
            }
        }
    }

    private fun transferStockInternal(fromProductId: Long, toProductId: Long, quantity: Int) {
        // 실제 재고 이전 로직
    }
}
```

### 공정 락 (Fair Lock)

대기 순서대로 락을 부여하는 공정 락:

```kotlin
@Service
class ProductService(
    private val redissonClient: RedissonClient
) {

    fun decreaseStockFair(productId: Long, quantity: Int): Product {
        val fairLock = redissonClient.getFairLock("fair:product:$productId")

        try {
            fairLock.lock(10, TimeUnit.SECONDS) // 10초 대기
            // 공정하게 순서대로 처리
            return decreaseStockInternal(productId, quantity)
        } finally {
            if (fairLock.isHeldByCurrentThread()) {
                fairLock.unlock()
            }
        }
    }
}
```

### 분산락 모니터링

```kotlin
@Component
class LockMonitor {

    private val lockAttempts = AtomicLong(0)
    private val lockFailures = AtomicLong(0)

    fun recordLockAttempt() {
        lockAttempts.incrementAndGet()
    }

    fun recordLockFailure() {
        lockFailures.incrementAndGet()
    }

    fun getLockStats(): LockStats {
        return LockStats(
            attempts = lockAttempts.get(),
            failures = lockFailures.get(),
            successRate = if (lockAttempts.get() > 0) {
                (lockAttempts.get() - lockFailures.get()) * 100.0 / lockAttempts.get()
            } else 0.0
        )
    }
}

data class LockStats(
    val attempts: Long,
    val failures: Long,
    val successRate: Double
)
```

## ⚠️ 주의사항

1. **분산락 TTL**: 작업 시간보다 충분히 긴 TTL 설정
2. **캐시 일관성**: 데이터 변경 시 캐시 무효화 필수
3. **락 충돌**: 높은 동시성 환경에서는 재시도 로직 고려
4. **메모리 사용**: Redis 메모리 사용량 모니터링
5. **멀티락 데드락**: 여러 락을 항상 같은 순서로 획득
6. **공정 락 성능**: 처리량은 떨어질 수 있지만 공정성 보장
7. **모니터링**: 락 획득 실패율과 대기 시간 모니터링 필수

## 🎉 장점

- ✅ **간단함**: 복잡한 설정 없이 바로 사용
- ✅ **실용성**: 실제 프로젝트에서 바로 적용 가능
- ✅ **확장성**: 다른 도메인 쉽게 추가
- ✅ **이해하기 쉬움**: 초보자도 쉽게 따라할 수 있음
- ✅ **성능**: Redis 캐시로 빠른 응답
- ✅ **안정성**: 분산락으로 동시성 제어