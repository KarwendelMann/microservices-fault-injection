package at.frumpold.faultinjector.service

import at.frumpold.faultinjector.dto.FaultDto

interface FaultActivationService {

    fun configureFault(fault: FaultDto)

    fun getFaultActivationById(faultId: String): FaultDto
}
