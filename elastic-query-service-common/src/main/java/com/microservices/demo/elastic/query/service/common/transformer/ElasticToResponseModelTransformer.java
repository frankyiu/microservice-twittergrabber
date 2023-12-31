package com.microservices.demo.elastic.query.service.common.transformer;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticToResponseModelTransformer {
    public ElasticQueryServiceResponseModel getResponseModel(TwitterIndexModel twitterIndexModel) {
        return ElasticQueryServiceResponseModel.builder()
                                               .id(twitterIndexModel.getId())
                                               .text(twitterIndexModel.getText())
                                               .userId(twitterIndexModel.getUserId())
                                               .createAt(twitterIndexModel.getCreatedAt())
                                               .build();
    }

    public List<ElasticQueryServiceResponseModel> getResponseModels(List<TwitterIndexModel> twitterIndexModelList){
        return twitterIndexModelList.stream().map(this::getResponseModel).collect(Collectors.toList());
    }

}

