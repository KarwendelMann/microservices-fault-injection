package at.frumpold.faultinjector.dto

data class FaultDto(
    val faultId: String,
    var isActivated: Boolean = false,
    val activationEndpoint: String? = null,

    // Fault Type Specific fields

    // Injected Faults -- Delay
    var delay: Int? = null,

    // Overload Faults
    var userCount: Int? = null,
    var userSpawnRate: Int? = null,

    // Configuration Faults
    var targetDeployment: String? = null,
    var pathToHealthyConfig: String? = null,
    var pathToFaultyConfig: String? = null,
)
