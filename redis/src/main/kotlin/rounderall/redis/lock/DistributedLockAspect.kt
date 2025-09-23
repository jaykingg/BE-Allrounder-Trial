package rounderall.redis.lock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.util.*

@Aspect
@Component
class DistributedLockAspect(
    private val redisLockManager: RedisLockManager
) {
    private val parser = SpelExpressionParser()

    @Around("@annotation(lock)")
    fun around(pjp: ProceedingJoinPoint, lock: DistributedLock): Any? {
        val key = resolveKey(lock.key, pjp)
        val token = UUID.randomUUID().toString()
        val waitUntil = System.currentTimeMillis() + lock.waitMillis

        while (System.currentTimeMillis() < waitUntil) {
            if (redisLockManager.tryLock(key, token, lock.leaseMillis)) {
                try {
                    return pjp.proceed()
                } finally {
                    redisLockManager.unlock(key, token)
                }
            }
            Thread.sleep(50)
        }
        throw IllegalStateException("락 획득 실패: $key")
    }

    private fun resolveKey(expression: String, pjp: ProceedingJoinPoint): String {
        // 지원 형태: "prefix:#{#paramName}"
        val start = expression.indexOf("#{")
        val end = expression.indexOf("}")
        if (start == -1 || end == -1 || end <= start) return expression
        val prefix = expression.substring(0, start)
        val spel = expression.substring(start + 2, end)

        val paramNames = (pjp.signature as org.aspectj.lang.reflect.MethodSignature).parameterNames
        val args = pjp.args
        val ctx = StandardEvaluationContext()
        paramNames.forEachIndexed { index, name -> ctx.setVariable(name, args[index]) }
        val value = parser.parseExpression(spel).getValue(ctx)
        return prefix + (value?.toString() ?: "null")
    }
}



