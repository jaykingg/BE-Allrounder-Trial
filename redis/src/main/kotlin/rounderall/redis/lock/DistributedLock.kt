package rounderall.redis.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val key: String,
    val waitMillis: Long = 1000,
    val leaseMillis: Long = 5000
)
