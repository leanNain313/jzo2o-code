package com.jzo2o.foundations.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfiguration {

    @Value("${jzo2o.es.host}")
    private String host;

    @Value("${jzo2o.es.port}")
    private Integer port;

    @Value("${jzo2o.es.agreement}")
    private String agreement;

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port,agreement))
        );
    }
}