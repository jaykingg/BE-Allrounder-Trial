package rounderall.security.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/security")
class SecurityController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "security",
            "message" to "Security 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "security",
            "features" to listOf(
                "RBAC (Role-Based Access Control)",
                "ABAC (Attribute-Based Access Control)",
                "PBAC (Policy-Based Access Control)",
                "JWT (JSON Web Token)"
            ),
            "port" to 8083
        )
    }
}
