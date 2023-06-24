package com.microservices.demo.reactive.elastic.query.web.client.config;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {


    private final ElasticQueryWebClientConfigData.WebClient webClient;

    public WebClientConfig(ElasticQueryWebClientConfigData webClientConfigData) {
        this.webClient = webClientConfigData.getWebClient();
    }


    @Bean("webClient")
    WebClient webClient() {
        return WebClient.builder()
                        .baseUrl(webClient.getBaseUri())
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, webClient.getContentType())
                        .defaultHeader(HttpHeaders.ACCEPT, webClient.getAcceptType())
                        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                        .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                                                                              .maxInMemorySize(webClient.getMaxInMemory()))
                        .build();
    }

    private TcpClient getTcpClient() {
        return TcpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClient.getConnectTimeoutMs())
                        .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(webClient.getReadTimeoutMs(), TimeUnit.MILLISECONDS)))
                        .doOnConnected(connection -> connection.addHandlerLast(new WriteTimeoutHandler(webClient.getWriteTimeoutMs(), TimeUnit.MILLISECONDS)));
    }
}
