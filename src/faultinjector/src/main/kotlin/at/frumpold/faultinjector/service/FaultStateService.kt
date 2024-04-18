package at.frumpold.faultinjector.service

interface FaultStateService {
    fun activateFault(faultId: String)

    fun deactivateFault(faultId: String)

    fun isFaultActivated(faultId: String): Boolean
}
