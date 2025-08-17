package rounderall.querydsl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/querydsl")
class QuerydslController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "querydsl",
            "message" to "QueryDSL 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "querydsl",
            "features" to listOf(
                "QueryDSL",
                "Type-safe Queries",
                "Dynamic Queries",
                "JPA Integration"
            ),
            "port" to 8086
        )
    }
}
