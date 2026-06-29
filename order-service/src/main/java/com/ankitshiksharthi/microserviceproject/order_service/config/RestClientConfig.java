package com.ankitshiksharthi.microserviceproject.order_service.config;

import com.ankitshiksharthi.microserviceproject.order_service.client.InventoryClient;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    @Value("${inventory.url}")
    private String inventoryServiceUrl;
    private final ObservationRegistry observationRegistry;

  @Bean
  @LoadBalanced
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder().observationRegistry(observationRegistry);
  }

  @Bean
  public InventoryClient inventoryClient(RestClient.Builder restClientBuilder) {
    RestClient restClient = restClientBuilder.baseUrl(inventoryServiceUrl).build();
        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(InventoryClient.class);
    }
}
