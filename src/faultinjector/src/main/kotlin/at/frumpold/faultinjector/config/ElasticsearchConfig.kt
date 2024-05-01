package at.frumpold.faultinjector.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
internal class ElasticsearchConfig : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration =
        ClientConfiguration.builder().connectedTo("elasticsearch.logging.svc.cluster.local:9200").build()

}
