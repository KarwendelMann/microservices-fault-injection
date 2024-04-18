package at.frumpold.faultinjector.service

import at.frumpold.faultinjector.dto.FaultDto

interface FaultActivationService {

    fun activateFault(faultId: String)

    fun deactivateFault(faultId: String)

    fun getFaultActivationById(faultId: String): FaultDto
}
