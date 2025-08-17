package rounderall.kafka.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/kafka")
class KafkaController {

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "module" to "kafka",
            "message" to "Kafka 모듈이 정상적으로 실행 중입니다."
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "module" to "kafka",
            "features" to listOf(
                "Apache Kafka",
                "Producer/Consumer",
                "Message Broker",
                "Event Streaming"
            ),
            "port" to 8084,
            "kafka_broker" to "localhost:9092"
        )
    }
}
