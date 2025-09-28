package rounderall.redis.lock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

@Aspect
@Component
class DistributedLockAspect(
    private val redisTemplate: RedisTemplate<String, String>
) {

    @Around("@annotation(distributedLock)")
    fun around(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock): Any? {
        val lockKey = distributedLock.key
        val lockValue = UUID.randomUUID().toString()
        
        return try {
            // 락 획득 시도
            val acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, distributedLock.leaseMillis, TimeUnit.MILLISECONDS)
            
            if (acquired == true) {
                try {
                    // 락 획득 성공 - 메서드 실행
                    joinPoint.proceed()
                } finally {
                    // 락 해제
                    releaseLock(lockKey, lockValue)
                }
            } else {
                // 락 획득 실패
                throw RuntimeException("분산락 획득 실패: $lockKey")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun releaseLock(lockKey: String, lockValue: String) {
        val script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
        """.trimIndent()
        
        redisTemplate.execute { connection ->
            connection.eval(script.toByteArray(), 1, lockKey.toByteArray(), lockValue.toByteArray())
        }
    }
}
