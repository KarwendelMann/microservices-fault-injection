package at.frumpold.faultinjector.controller

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultActivationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/faults")
class FaultActivationController(
    private val faultActivationService: FaultActivationService
) {
    @PostMapping("/activate/{faultId}")
    fun activateFault(@PathVariable faultId: String) = faultActivationService.activateFault(faultId)

    @PostMapping("/deactivate/{faultId}")
    fun deactivateFault(@PathVariable faultId: String) = faultActivationService.deactivateFault(faultId)

    @GetMapping("/{faultId}")
    fun getFaultById(@PathVariable faultId: String): FaultDto = faultActivationService.getFaultActivationById(faultId)

}
