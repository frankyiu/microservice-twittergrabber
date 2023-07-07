package com.microservices.demo.elastic.query.service.model;


import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElasticQueryServiceWordCountResponseModel {
    private String word;
    private Long wordCount;

}
