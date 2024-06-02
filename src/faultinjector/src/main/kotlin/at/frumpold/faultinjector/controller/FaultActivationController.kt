package at.frumpold.faultinjector.controller

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultActivationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/faults")
internal class FaultActivationController(
    private val faultActivationService: FaultActivationService
) {
    @PostMapping("/configure")
    fun configure(@RequestBody fault: FaultDto) = faultActivationService.configureFault(fault)

    @GetMapping("/{faultId}")
    fun getFaultById(@PathVariable faultId: String): FaultDto = faultActivationService.getFaultActivationById(faultId)

}
