package at.frumpold.faultinjector.dto

open class FaultDto(
    val faultId: String,
    var isActivated: Boolean,
    val activationEndpoint: String? = null,

    // Fault Type Specific fields

    // Injected Faults -- Delay
    var delay: Int? = null,

    // Overload Faults
    var userCount: Int? = null,
    var userSpawnRate: Int? = null,
)
