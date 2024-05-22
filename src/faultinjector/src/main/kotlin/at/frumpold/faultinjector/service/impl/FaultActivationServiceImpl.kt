package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultActivationService
import at.frumpold.faultinjector.service.FaultStateService
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
internal class FaultActivationServiceImpl(
    private val faultStateService: FaultStateService
) : FaultActivationService {
    override fun activateFault(faultId: String) = faultStateService.activateFault(faultId).also {
        logger.info { "Fault '$faultId' deactivated" }
    }

    override fun deactivateFault(faultId: String) = faultStateService.deactivateFault(faultId).also {
        logger.info { "Fault '$faultId' activated" }
    }

    override fun getFaultActivationById(faultId: String): FaultDto =
        faultStateService.getFault(faultId)

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
