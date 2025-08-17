package rounderall.batchscheduler.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/batch")
class BatchController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "batch-scheduler",
            "message" to "Batch Scheduler 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "batch-scheduler",
            "features" to listOf(
                "Spring Batch",
                "Quartz Scheduler",
                "Job Configuration",
                "Reader/Processor/Writer"
            ),
            "port" to 8081
        )
    }
}
