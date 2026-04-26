package com.jzo2o.foundations.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class EsConfiguration {

    @Value("${jzo2o.es.host}")
    private String host;

    @Value("${jzo2o.es.port}")
    private Integer port;

    @Value("${jzo2o.es.agreement}")
    private String agreement;

    @Value("${jzo2o.es.username:}")
    private String username;

    @Value("${jzo2o.es.password:}")
    private String password;

    @Bean
    public RestHighLevelClient client(){
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, agreement));
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return new RestHighLevelClient(builder);
    }
}
