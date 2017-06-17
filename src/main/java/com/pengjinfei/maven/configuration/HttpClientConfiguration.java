package com.pengjinfei.maven.configuration;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created on 6/17/17
 *
 * @author Pengjinfei
 */
@Configuration
public class HttpClientConfiguration {

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig defaultRequestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectionRequestTimeout(2000)
                .setConnectionRequestTimeout(2000)
                .setSocketTimeout(2000)
                .setContentCompressionEnabled(false)
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(2);
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .disableContentCompression()
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient()));
    }
}
