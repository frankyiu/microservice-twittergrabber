package com.microservices.demo.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

    private final WebClient.Builder webClientBuilder;

    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder, ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClientBuilder = webClientBuilder;
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }


    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel model) {
        LOG.info("Querying by text {}", model.getText());
        return getWebClient(model).bodyToFlux(ElasticQueryWebClientResponseModel.class)
                                  .collectList()
                                  .block();
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel model) {
        return webClientBuilder.build()
                               .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText()
                                                                                         .getMethod()))
                               .uri(elasticQueryWebClientConfigData.getQueryByText()
                                                                   .getUri())
                               .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText()
                                                                                        .getAccept()))
                               .body(BodyInserters.fromPublisher(Mono.just(model), createParameterizedYpeReference()))
                               .retrieve()
                               .onStatus(httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                                       clientResponse -> Mono.just(new BadCredentialsException("Not authenticated")))
                               .onStatus(HttpStatus::is4xxClientError,
                                       clientResponse -> Mono.just(new ElasticQueryWebClientException(clientResponse.statusCode()
                                                                                                                    .getReasonPhrase())))
                               .onStatus(HttpStatus::is5xxServerError,
                                       clientResponse -> Mono.just(new Exception(clientResponse.statusCode()
                                                                                               .getReasonPhrase())));
    }

    private <T> ParameterizedTypeReference<T> createParameterizedYpeReference() {
        return new ParameterizedTypeReference<T>() {
        };
    }

}
