package com.thirdeye3.usermanager.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EurekaClientConfig {

    @Value("${thirdeye.api.key}")
    private String apiKey;

    @Bean
    public RestTemplateCustomizer eurekaRestTemplateCustomizer() {
        return new RestTemplateCustomizer() {
            @Override
            public void customize(RestTemplate restTemplate) {
                List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
                interceptors.add((request, body, execution) -> {
                    request.getHeaders().add("THIRDEYE-API-KEY", apiKey);
                    return execution.execute(request, body);
                });
                restTemplate.setInterceptors(interceptors);
            }
        };
    }
}