package rounderall.batchscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BatchSchedulerApplication

fun main(args: Array<String>) {
    runApplication<BatchSchedulerApplication>(*args)
}
