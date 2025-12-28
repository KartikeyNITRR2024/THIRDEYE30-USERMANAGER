package com.thirdeye3.usermanager.externalcontollers;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StockViewerClient {

    private static final Logger logger = LoggerFactory.getLogger(StockViewerClient.class);
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;

    public StockViewerClient(DiscoveryClient discoveryClient, WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClient = webClientBuilder.build();
    }

    public Response<Boolean> updateOrAddThresholdGroupDto(ThresholdGroupDto thresholdGroupDto) {
        List<ServiceInstance> instances = discoveryClient.getInstances("THIRDEYE30-STOCKVIEWER");

        if (instances.isEmpty()) {
            logger.error("No instances found for THIRDEYE30-STOCKVIEWER");
            return new Response<>(false, 500, "No instances found", false);
        }

        ParameterizedTypeReference<Response<Boolean>> typeRef = 
                new ParameterizedTypeReference<Response<Boolean>>() {};

        List<Response<Boolean>> results = Flux.fromIterable(instances)
                .flatMap(instance -> {
                    String url = instance.getUri().toString() + "/sv/update/thresholds";
                    return webClient.post()
                            .uri(url)
                            .bodyValue(thresholdGroupDto)
                            .retrieve()
                            .bodyToMono(typeRef)
                            .onErrorResume(e -> {
                                logger.error("Failed to update instance: {} - {}", instance.getUri(), e.getMessage());
                                return Mono.just(new Response<>(false, 500, e.getMessage(), false));
                            });
                })
                .collectList()
                .block();

        boolean allSuccess = results != null && !results.isEmpty() && results.stream()
                .allMatch(res -> res != null && res.isSuccess() && Boolean.TRUE.equals(res.getResponse()));

        return new Response<>(allSuccess, allSuccess ? 0 : 500, 
                allSuccess ? "All instances updated" : "Partial failure in broadcast", allSuccess);
    }
}