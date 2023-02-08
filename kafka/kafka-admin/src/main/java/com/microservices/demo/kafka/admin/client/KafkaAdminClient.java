package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final AdminClient adminClient;
    private final RetryConfigData retryConfigData;
    private final KafkaConfigData kafkaConfigData;

    private final RetryTemplate retryTemplate;

    private final WebClient webClient;

    public KafkaAdminClient(AdminClient adminClient, RetryConfigData retryConfigData, KafkaConfigData kafkaConfigData, RetryTemplate retryTemplate, WebClient webClient) {
        this.adminClient = adminClient;
        this.retryConfigData = retryConfigData;
        this.kafkaConfigData = kafkaConfigData;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reach max number of retry for creating kafka topics", t);
        }
        checkTopicCreated();
    }

    public void checkTopicCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        int maxAttempt = retryConfigData.getMaxAttempt();
        int multiplier = retryConfigData.getMultiplier()
                                        .intValue();
        long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topic, topics)) {
                checkMaxRetry(retryCount++, maxAttempt);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        int maxAttempt = retryConfigData.getMaxAttempt();
        int multiplier = retryConfigData.getMultiplier()
                                        .intValue();
        long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxAttempt);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient.method(HttpMethod.GET)
                            .uri(kafkaConfigData.getSchemaRegistryUrl())
                            .exchange()
                            .map(ClientResponse::statusCode)
                            .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void checkMaxRetry(int retry, int maxAttempt) {
        if (retry > maxAttempt)
            throw new KafkaClientException("Reach max retry in reading kafka topics");
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for creating new topics ");
        }
    }

    private boolean isTopicCreated(String topicName, Collection<TopicListing> topics) {
        if (topics == null) return false;
        return topics.stream()
                     .anyMatch(topic -> topic.name()
                                             .equals(topicName));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        LOG.info("Creating {} topics attempt {}", topicNames.size(), retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNames.stream()
                                               .map(topic -> new NewTopic(topic, kafkaConfigData.getNumOfPartition(), kafkaConfigData.getReplicationFactor()))
                                               .collect(Collectors.toList());
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reach max number of retry for getting kafka topics", t);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        LOG.info("Reading kafka topic {} attempt {}", kafkaConfigData.getTopicNamesToCreate()
                                                                     .toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics()
                                                     .listings()
                                                     .get();
        if (topics != null) {
            topics.forEach(topic -> LOG.info("Topic with names {}", topic));
        }
        return topics;
    }


}
