package rounderall.redis.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/redis")
class RedisController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "redis",
            "message" to "Redis 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "redis",
            "features" to listOf(
                "Redis Caching",
                "Feature Flag",
                "Session Management",
                "In-Memory Database"
            ),
            "port" to 8087,
            "redis_host" to "localhost:6379"
        )
    }
}
