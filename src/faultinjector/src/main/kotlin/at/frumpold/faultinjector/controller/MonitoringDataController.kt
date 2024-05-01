package at.frumpold.faultinjector.controller

import at.frumpold.faultinjector.service.ElasticsearchService
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("data")
internal class MonitoringDataController(
    private val elasticsearchService: ElasticsearchService
) {

    @GetMapping("/since", produces = ["application/zip"])
    fun getMonitoringDataSince(
        @RequestParam timestamp: String,
    ): ResponseEntity<Any> {
        logger.info { "Pulling Data for timestamp $timestamp" }
        return try {
            val timestampParsed = Instant.parse(timestamp)
            val data = elasticsearchService.getDataSince(timestampParsed)

            val headers = HttpHeaders()
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data_since_$timestamp.zip\"")
            ResponseEntity.ok()
                .headers(headers)
                .body(data)
        } catch (e: DateTimeParseException) {
            ResponseEntity.badRequest().body("Invalid timestamp format. Use ISO-8601")
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
