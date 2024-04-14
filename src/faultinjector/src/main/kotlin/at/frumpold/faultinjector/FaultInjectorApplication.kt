package at.frumpold.faultinjector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FaultInjectorApplication

fun main(args: Array<String>) {
	runApplication<FaultInjectorApplication>(*args)
}
