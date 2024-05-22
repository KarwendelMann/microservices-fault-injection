package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.dto.InjectedFaultDelayDto
import at.frumpold.faultinjector.service.FaultStateService
import org.springframework.stereotype.Service

@Service
internal class FaultStateServiceImpl : FaultStateService {

    private val faults = listOf<FaultDto>(
        InjectedFaultDelayDto("delay-fault-1", false, 5000)
    )
    override fun activateFault(faultId: String) {
        faults.find { it.faultId == faultId }?.isActivated = true
    }

    override fun deactivateFault(faultId: String) {
        faults.find { it.faultId == faultId }?.isActivated = false
    }

    override fun getFault(faultId: String) = faults.find { it.faultId == faultId } ?: error("fault $faultId is not specified")
}
