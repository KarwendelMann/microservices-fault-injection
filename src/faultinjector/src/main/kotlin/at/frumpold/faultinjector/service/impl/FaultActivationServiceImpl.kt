package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultActivationService
import at.frumpold.faultinjector.service.FaultStateService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
internal class FaultActivationServiceImpl(
    private val faultStateService: FaultStateService,
    private val restTemplate: RestTemplate
) : FaultActivationService {
    override fun configureFault(fault: FaultDto) {
        faultStateService.configureFault(fault).also {
            logger.info { "Fault '${it.faultId}' configured: $it" }

            sendFaultConfigurationRequest(it)
        }
    }

    override fun getFaultActivationById(faultId: String): FaultDto =
        faultStateService.getFault(faultId)

    private fun sendFaultConfigurationRequest(fault: FaultDto) {
        val url = fault.activationEndpoint
        if (url != null) {
            try {
                val response = restTemplate.postForEntity(url, fault, String::class.java)
                logger.info { "Successfully sent Fault Activation request to $url. Response: ${response.body}" }
            } catch (e: Exception) {
                logger.error { "Error sending POST request to $url: ${e.message}" }
                throw e
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
