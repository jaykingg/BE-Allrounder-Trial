package rounderall.architecture.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/architecture")
class ArchitectureController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "architecture",
            "message" to "Architecture 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "architecture",
            "features" to listOf(
                "Hexagonal Architecture",
                "Domain-Driven Design (DDD)",
                "Clean Architecture",
                "Ports and Adapters"
            ),
            "port" to 8082
        )
    }
}
