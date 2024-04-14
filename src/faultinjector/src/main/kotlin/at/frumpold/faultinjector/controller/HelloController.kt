package at.frumpold.faultinjector.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    fun sayHello(): String {
        return "Hello, Fault Injection Service is running!"
    }
}
