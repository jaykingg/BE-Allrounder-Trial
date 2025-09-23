package rounderall.redis.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val key: String,                  // 예: "product:#{#productId}"
    val waitMillis: Long = 3_000,     // 대기 최대시간
    val leaseMillis: Long = 10_000    // 자동 만료시간
)



