package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.service.FaultStateService
import org.springframework.stereotype.Service

@Service
internal class FaultStateServiceImpl : FaultStateService {

    private val faults: MutableMap<String, Boolean> = mutableMapOf(
        "testFault" to false
    )
    override fun activateFault(faultId: String) {
        faults[faultId] = true
    }

    override fun deactivateFault(faultId: String) {
        faults[faultId] = false
    }

    override fun isFaultActivated(faultId: String) = faults[faultId] ?: error("fault $faultId is not specified")
}
