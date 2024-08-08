package at.frumpold.faultinjector.service

import at.frumpold.faultinjector.dto.FaultDto

interface KubernetesFaultService {

    fun changeKubernetesConfiguration(faultDto: FaultDto)
}
