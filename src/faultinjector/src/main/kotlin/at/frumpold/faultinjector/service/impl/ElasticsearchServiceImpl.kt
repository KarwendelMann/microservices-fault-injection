package at.frumpold.faultinjector.service.impl

import at.frumpold.faultinjector.service.ElasticsearchService
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

const val FILEBEAT_INDEX_PATTERN = "filebeat-*"
const val METRICBEAT_INDEX_PATTERN = "metricbeat-*"
const val JAEGER_INDEX_PATTERN = "jaeger-span-*"

@Service
internal class ElasticsearchServiceImpl(
    private val operations: ElasticsearchOperations,
    private val objectMapper: ObjectMapper
) : ElasticsearchService {
    override fun getDataSince(timestamp: Instant): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ZipOutputStream(byteArrayOutputStream).use { zipOutputStream ->
            val indices = listOf(FILEBEAT_INDEX_PATTERN, METRICBEAT_INDEX_PATTERN, JAEGER_INDEX_PATTERN)

            indices.forEach { index ->
                val groupedByPod = searchIndex(index, timestamp)
                    .let { results ->
                        when (index) {
                            FILEBEAT_INDEX_PATTERN -> groupByKubernetesApp(results)
                            METRICBEAT_INDEX_PATTERN -> groupByPrometheusPod(results)
                            JAEGER_INDEX_PATTERN -> groupByJaegerService(results)
                            else -> error("Unknown Index $index")
                        }
                    }

                groupedByPod.forEach { (podName, dataPoints) ->
                    val fileName = "${index.filter { it.isLetterOrDigit() }}/$podName.json"
                    zipOutputStream.putNextEntry(ZipEntry(fileName))
                    dataPoints.forEach { dataPoint ->
                        zipOutputStream.write((objectMapper.writeValueAsString(dataPoint) + "\n").toByteArray())
                    }
                    zipOutputStream.closeEntry()
                }
            }
        }

        return byteArrayOutputStream.toByteArray()
    }

    private fun groupByKubernetesApp(results: List<Map<String, Any>>) = results.groupBy {
        (it["kubernetes"] as? Map<*, *>)?.get("labels")?.let { labels ->
            (labels as? Map<*, *>)?.get("app") as? String ?: error("kubernetes.labels.app not found")
        } ?: error("kubernetes.labels.app not found")
    }

    private fun groupByPrometheusPod(results: List<Map<String, Any>>) = results.groupBy {
        (it["prometheus"] as? Map<*, *>)?.get("labels")?.let { labels ->
            (labels as? Map<*, *>)?.get("pod") as? String ?: error("prometheus.labels.pod not found")
        } ?: error("prometheus.labels.pod not found")
    }

    private fun groupByJaegerService(results: List<Map<String, Any>>) = results.groupBy {
        (it["process"] as? Map<*, *>)?.get("serviceName") as String? ?: error("process.serviceName not found when grouping for jaeger")
    }

    private fun searchIndex(indexPattern: String, timestamp: Instant): List<Map<String, Any>> {
        val criteria =
            Criteria(if (indexPattern == JAEGER_INDEX_PATTERN) "startTime" else "@timestamp")
                .greaterThanEqual(if (indexPattern == JAEGER_INDEX_PATTERN) timestamp.toEpochMilli() else timestamp.toString())
        val query = CriteriaQuery(criteria)
        val indexCoordinates = IndexCoordinates.of(indexPattern)

        return operations
            .search(query, Map::class.java, indexCoordinates)
            .searchHits
            .map {
                objectMapper.convertValue(it.content, Map::class.java) as Map<String, Any>
            }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
