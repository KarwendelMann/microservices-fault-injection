package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.FaultActivationService
import at.frumpold.faultinjector.service.FaultStateService
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.Yaml
import mu.KotlinLogging
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

@Service
internal class FaultActivationServiceImpl(
    private val faultStateService: FaultStateService,
    private val restTemplate: RestTemplate,
    private val resourceLoader: ResourceLoader
) : FaultActivationService {

    private val kubernetesApiClient = ClientBuilder.standard().build()
    private val kubernetesAppsApi = AppsV1Api(kubernetesApiClient)

    override fun configureFault(fault: FaultDto) {
        faultStateService.configureFault(fault).also {
            logger.info { "Fault '${it.faultId}' configured: $it" }

            sendFaultConfigurationRequest(it)

            changeKubernetesConfiguration(it)
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

    private fun changeKubernetesConfiguration(faultDto: FaultDto) {
        if (faultDto.targetDeployment != null && faultDto.pathToHealthyConfig != null && faultDto.pathToFaultyConfig != null) {
            val fileName = if (faultDto.isActivated) faultDto.pathToFaultyConfig else faultDto.pathToHealthyConfig
            if (fileName == null)
                return

            val fileContent = readResource(fileName)

            val deployment =
                Yaml.load(
                    fileContent
                ) as V1Deployment

            kubernetesAppsApi.replaceNamespacedDeployment(
                faultDto.targetDeployment,
                "default",
                deployment
            ).execute()
        }
    }

    private fun readResource(fileName: String): String {
        val resource: Resource = resourceLoader.getResource("classpath:$fileName")
        resource.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                return reader.lines().collect(Collectors.joining("\n"))
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
