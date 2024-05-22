package at.frumpold.faultinjector.service

import at.frumpold.faultinjector.dto.FaultDto

interface FaultStateService {
    fun activateFault(faultId: String)

    fun deactivateFault(faultId: String)

    fun getFault(faultId: String): FaultDto
}
