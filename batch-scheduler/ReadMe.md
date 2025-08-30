## Batch Flow

```text
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Reader        │    │   Processor     │    │   Writer        │
│                 │    │                 │    │                 │
│   전날 활동 로그   │───▶│     데이터 누적    │───▶│      요약 데이터   │
│   조회           │    │     그룹화        │    │     생성 및 저장   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Job 설정

```kotlin
@Bean
fun userActivitySummaryJob(): Job {
    return JobBuilder("userActivitySummaryJob", jobRepository)
        .start(userActivitySummaryStep())
        .build()
}
```

### Step 설정

```kotlin
@Bean
fun userActivitySummaryStep(): Step {
    return StepBuilder("userActivitySummaryStep", jobRepository)
        .chunk<UserActivityLog, UserActivitySummary>(10, transactionManager) // 10건씩 처리
        .reader(userActivityLogReader())
        .processor(userActivityLogProcessor())
        .writer(userActivitySummaryWriter())
        .build()
}
```

- Chunk 처리:
    - 10건씩 묶어서 처리 (메모리 효율성)
    - 각 Chunk는 하나의 트랜잭션으로 처리
    - 오류 발생 시 해당 Chunk만 롤

### 1단계: Reader (데이터 읽기)

- Reader의 역할:
    - 전날(어제)의 사용자 활동 로그를 데이터베이스에서 조회
    - Spring Batch가 한 건씩 읽어서 Processor로 전달
    - 모든 데이터를 읽으면 null을 반환하여 작업 종료 신호

### 2단계: Processor (데이터 처리)

- Processor의 역할:
    - Reader에서 읽은 각 활동 로그를 사용자별로 그룹화
    - 실제 요약 데이터 생성은 Writer에서 수행
    - 여기서는 데이터를 누적만 하고 null 반환

### 3단계: Writer (데이터 저장)

- Writer의 역할:
    - 기존 요약 데이터 삭제 (중복 방지)
    - 전날의 모든 활동 로그를 사용자별로 그룹화
    - 각 사용자별로 통계 계산:
        - 총 활동 수
        - 고유 활동 유형 수
        - 마지막 활동 시간
    - 계산된 요약 데이터를 데이터베이스에 저장

### Scheduler 설정

```kotlin
@Component
class UserActivityScheduler(
    private val jobLauncher: JobLauncher,
    private val userActivitySummaryJob: Job
) {

    @Scheduled(cron = "0 0 1 * * ?") // 매일 자정 1시에 실행
    fun runUserActivitySummaryJob() {
        try {
            val jobParameters = JobParametersBuilder()
                .addString("executionTime", LocalDateTime.now().toString())
                .toJobParameters()

            val execution = jobLauncher.run(userActivitySummaryJob, jobParameters)
            println("배치 작업 실행 완료: ${execution.status}")
        } catch (e: Exception) {
            println("배치 작업 실행 실패: ${e.message}")
        }
    }
}
```

---

## Summary

- **Chunk 기반 처리**
    - 대용량 데이터를 작은 단위로 나누어 처리
    - 메모리 효율성과 트랜잭션 관리 개선
- ItemReader/Processor/Writer 패턴
    - Reader: 데이터 소스에서 데이터 읽기
    - Processor: 비즈니스 로직 처리
    - Writer: 처리된 데이터 저장
- 트랜잭션 관리
    - 각 Chunk는 독립적인 트랜잭션
    - 오류 발생 시 해당 Chunk만 롤백
- 스케줄링
    - Cron 표현식을 사용한 정기적 실행
    - Spring의 @Scheduled 어노테이션 활용

---

## Notes

### Crons

```kt
/**
 * 매일 새벽 1시 0분 0초에 실행
 * cron = "0 0 1 * * ?"
 *   ┌───────────── 초 (0초)
 *   │ ┌─────────── 분 (0분)
 *   │ │ ┌───────── 시 (1시)
 *   │ │ │ ┌─────── 일 (매일)
 *   │ │ │ │ ┌───── 월 (매월)
 *   │ │ │ │ │ ┌─── 요일 (무관)
 *   │ │ │ │ │ │
 *   0 0 1 * * ?
 */
@Scheduled(cron = "0 0 1 * * ?")
public void runDailyAt1AM() {
    // 실행 로직
}
```

```kt
/**
 * 매 분 0초마다 실행
 * cron = "0 * * * * ?"
 *   ┌───────────── 초 (0초)
 *   │ ┌─────────── 분 (매 분)
 *   │ │ ┌───────── 시 (매 시)
 *   │ │ │ ┌─────── 일 (매일)
 *   │ │ │ │ ┌───── 월 (매월)
 *   │ │ │ │ │ ┌─── 요일 (무관)
 *   │ │ │ │ │ │
 *   0 * * * * ?
 */
@Scheduled(cron = "0 * * * * ?")
public void runEveryMinute() {
    // 실행 로직
}
```

### CommandLineRunner

CommandLineRunner는 Spring Boot에서 제공하는 인터페이스로,
애플리케이션이 완전히 시작된 후 자동으로 실행되는 코드를 작성할 수 있게 해주는 기능

```kotlin
fun initData(userActivityLogRepository: UserActivityLogJpaRepository): CommandLineRunner {
    ...
```

---