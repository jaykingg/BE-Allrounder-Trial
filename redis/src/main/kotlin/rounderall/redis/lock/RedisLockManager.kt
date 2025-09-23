package rounderall.redis.lock

import org.springframework.dao.DataAccessException
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*

@Component
class RedisLockManager(
    private val redisTemplate: StringRedisTemplate
) {

    private val unlockScript = DefaultRedisScript<Long>(
        """
        if redis.call('get', KEYS[1]) == ARGV[1] then
            return redis.call('del', KEYS[1])
        else
            return 0
        end
        """.trimIndent(),
        Long::class.java
    )

    fun tryLock(key: String, token: String, leaseMillis: Long): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(key, token, Duration.ofMillis(leaseMillis)) == true
    }

    fun unlock(key: String, token: String) {
        redisTemplate.execute(unlockScript, listOf(key), token)
    }
}



