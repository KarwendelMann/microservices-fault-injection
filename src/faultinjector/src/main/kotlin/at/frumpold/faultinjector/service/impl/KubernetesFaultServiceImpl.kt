package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.dto.FaultDto
import at.frumpold.faultinjector.service.KubernetesFaultService
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.Yaml
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

@Service
internal class KubernetesFaultServiceImpl(
    private val resourceLoader: ResourceLoader
) : KubernetesFaultService {

    private val kubernetesApiClient = ClientBuilder.standard().build()
    private val kubernetesAppsApi = AppsV1Api(kubernetesApiClient)
    override fun changeKubernetesConfiguration(faultDto: FaultDto) {
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
}
