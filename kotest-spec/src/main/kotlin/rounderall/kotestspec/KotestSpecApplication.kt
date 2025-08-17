package rounderall.kotestspec

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotestSpecApplication

fun main(args: Array<String>) {
    runApplication<KotestSpecApplication>(*args)
}
