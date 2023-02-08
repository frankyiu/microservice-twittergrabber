package com.microservices.demo.kafka.producer.config.service.impl;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.annotation.PreDestroy;

@Service
public class TwitterKafkaProducer  implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PreDestroy
    public void close(){
        if(kafkaTemplate!=null){
            LOG.info("Closing kafka producer");
            kafkaTemplate.destroy();
        }
    }

    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.info("Sending message {} to topic {}", message, topicName);
        ListenableFuture<SendResult<Long, TwitterAvroModel>> listenableFuture = kafkaTemplate.send(topicName, key, message);
        listenableFuture.addCallback(new ListenableFutureCallback<>(){

                                         @Override
                                         public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                                             RecordMetadata recordMetaData = result.getRecordMetadata();
                                             LOG.debug("Received new metaData, Topic {}; Partition {}; Offset {}; Timestamp {}; at time {}",
                                                     recordMetaData.topic(),
                                                     recordMetaData.partition(),
                                                     recordMetaData.offset(),
                                                     recordMetaData.timestamp(),
                                                     System.nanoTime());
                                         }

                                         @Override
                                         public void onFailure(Throwable ex) {
                                             LOG.error("Error while sending message {} to topic {}", message.toString(), topicName, ex);
                                         }
                                     }
        );
    }
}
