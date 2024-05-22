package at.frumpold.faultinjector.dto

data class InjectedFaultDelayDto(
    override val faultId: String,
    override var isActivated: Boolean,
    val delay: Int?,
) : FaultDto(
    faultId, isActivated
)
