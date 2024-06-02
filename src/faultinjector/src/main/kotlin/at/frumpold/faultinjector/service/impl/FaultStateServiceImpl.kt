package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultStateService
import org.springframework.stereotype.Service

const val DELAY_FAULT_1 = "delay-fault-1"
const val OVERLOAD_FAULT_1 = "overload-fault-1"

@Service
internal class FaultStateServiceImpl : FaultStateService {

    private val faults = mutableMapOf<String, FaultDto>(
        DELAY_FAULT_1 to FaultDto(
            faultId = DELAY_FAULT_1,
            isActivated = false,
            activationEndpoint = null,
            delay = 1000
        ),
        OVERLOAD_FAULT_1 to FaultDto(
            faultId = OVERLOAD_FAULT_1,
            isActivated = false,
            activationEndpoint = "http://loadgenerator.default.svc.cluster.local:8089/control",
            userCount = 10,
            userSpawnRate = 10
        )
    )

    override fun configureFault(fault: FaultDto): FaultDto {
        val f = faults[fault.faultId] ?: error("fault with id ${fault.faultId} has no implementation")

        f.isActivated = fault.isActivated
        f.delay = fault.delay
        f.userCount = fault.userCount
        f.userSpawnRate = fault.userSpawnRate

        return f
    }


    override fun getFault(faultId: String) = faults[faultId] ?: error("fault $faultId is not specified")
}
