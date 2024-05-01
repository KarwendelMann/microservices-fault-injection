package at.frumpold.faultinjector.service

import java.time.Instant

interface ElasticsearchService {

    fun getDataSince(timestamp: Instant): ByteArray
}
