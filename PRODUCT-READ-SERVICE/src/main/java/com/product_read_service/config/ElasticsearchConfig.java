package com.product_read_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ReactiveElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Override
    public ClientConfiguration clientConfiguration() {
        // Splitting "http://localhost:9200" into "localhost:9200" if necessary
        String hostAndPort = elasticsearchUri.replace("http://", "").replace("https://", "");

        return ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .build();
    }
}
