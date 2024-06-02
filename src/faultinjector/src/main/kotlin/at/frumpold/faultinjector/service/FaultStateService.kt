package at.frumpold.faultinjector.service

import at.frumpold.faultinjector.dto.FaultDto

interface FaultStateService {
    fun configureFault(fault: FaultDto): FaultDto

    fun getFault(faultId: String): FaultDto
}
