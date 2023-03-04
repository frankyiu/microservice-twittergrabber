package com.microservices.demo.elastic.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.microservices.demo.config.ElasticConfigData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.microservices.demo.elastic")
public class ElasticsearchConfig  {

    private final ElasticConfigData elasticConfigData;

    public ElasticsearchConfig(ElasticConfigData elasticConfigData) {
        this.elasticConfigData = elasticConfigData;
    }

//    @Bean
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        UriComponents uri = UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl())
//                                                .build();
//        RestClient httpClient = RestClient.builder(new HttpHost(Objects.requireNonNull(uri.getHost()),
//                                                  uri.getPort(),
//                                                  uri.getScheme()))
//                                          .setRequestConfigCallback(cb -> cb.setConnectTimeout(elasticConfigData.getConnectTimeoutMs())
//                                                                            .setSocketTimeout(elasticConfigData.getSocketTimeoutMs()))
//                                          .build();
//
//        return new RestHighLevelClientBuilder(httpClient).setApiCompatibilityMode(true)
//                                                         .build();
//    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(elasticsearchTransport());
    }


    @Bean
    public ElasticsearchTransport elasticsearchTransport() {
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl())
                                                .build();
        return new RestClientTransport(
                RestClient.builder(new HttpHost(
                                  Objects.requireNonNull(uri.getHost()),
                                  uri.getPort(),
                                  uri.getScheme()
                          ))
                          .setRequestConfigCallback(cb -> cb.setConnectTimeout(elasticConfigData.getConnectTimeoutMs())
                                                            .setSocketTimeout(elasticConfigData.getSocketTimeoutMs()))
                          .build()
                , new JacksonJsonpMapper()
        );
    }

//    @Bean
//    public ElasticsearchOperations elasticsearchOperations() {
//        return new ElasticsearchRestTemplate(elasticsearchClient());
//    }
}
