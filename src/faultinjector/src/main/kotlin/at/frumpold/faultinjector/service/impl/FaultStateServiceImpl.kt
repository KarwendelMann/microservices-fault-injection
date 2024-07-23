package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultStateService
import org.springframework.stereotype.Service

const val DELAY_FAULT_1 = "delayFault1"
const val OVERLOAD_FAULT_1 = "overloadFault1"
const val INTERNAL_FAULT_1 = "internalFault1"
const val DEPENDENCY_FAULT_1 = "dependencyFault1"
const val CONFIGURATION_FAULT_1 = "configurationFault1"
const val DELAY_FAULT_2 = "delayFault2"


@Service
internal class FaultStateServiceImpl : FaultStateService {

    private val faults = mutableMapOf<String, FaultDto>(
        DELAY_FAULT_1 to FaultDto(
            faultId = DELAY_FAULT_1,
            activationEndpoint = null,
            delay = 1000
        ),
        OVERLOAD_FAULT_1 to FaultDto(
            faultId = OVERLOAD_FAULT_1,
            activationEndpoint = "http://loadgenerator.default.svc.cluster.local:8089/control",
            userCount = 10,
            userSpawnRate = 10
        ),
        INTERNAL_FAULT_1 to FaultDto(
            faultId = INTERNAL_FAULT_1,
        ),
        DEPENDENCY_FAULT_1 to FaultDto(
            faultId = DEPENDENCY_FAULT_1
        ),
        CONFIGURATION_FAULT_1 to FaultDto(
            faultId = CONFIGURATION_FAULT_1,
            targetDeployment = "redis-cart",
            pathToHealthyConfig = "redis.yaml",
            pathToFaultyConfig = "redis-faulty.yaml"
        ),
        DELAY_FAULT_2 to FaultDto(
            faultId = DELAY_FAULT_2,
            delay = 60,
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
