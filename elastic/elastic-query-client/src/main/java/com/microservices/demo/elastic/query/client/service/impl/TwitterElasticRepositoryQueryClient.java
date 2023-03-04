package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.common.util.CollectionsUtil;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.repository.TwitterElasticQueryRepository;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final TwitterElasticQueryRepository twitterElasticQueryRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticQueryRepository twitterElasticQueryRepository) {
        this.twitterElasticQueryRepository = twitterElasticQueryRepository;
    }


    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> result = twitterElasticQueryRepository.findById(id);
        LOG.info("Document with id {} retrieved successfully",
                result.orElseThrow(() -> new ElasticQueryClientException("No document found with id " + id))
                      .getId());
        return result.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> result = twitterElasticQueryRepository.findByText(text);
        LOG.info("{} of documents with text {} retrieved successfully", result.size(), text);
        return result;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        List<TwitterIndexModel> result = CollectionsUtil.getInstance()
                                                        .getListFromIterable(twitterElasticQueryRepository.findAll());
        LOG.info("{} of documents retrieved successfully", result.size());
        return result;
    }
}
