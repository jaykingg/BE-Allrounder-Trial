package rounderall.kotestspec.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/kotest")
class KotestController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "kotest-spec",
            "message" to "Kotest Spec 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "kotest-spec",
            "features" to listOf(
                "Kotest",
                "MockK",
                "Fixture Monkey",
                "BDD Specification",
                "Integration Test",
                "Unit Test",
                "Test Container"
            ),
            "port" to 8085
        )
    }
}
