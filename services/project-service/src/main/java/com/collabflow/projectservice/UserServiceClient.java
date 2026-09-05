package com.collabflow.projectservice;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {
    private final RestClient restClient;

    public UserServiceClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://USER-SERVICE")
                .build();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "teamExistsFallback")
    @Retry(name = "userService")
    public boolean teamExists(Long teamId) {
        Boolean result = restClient.get()
                .uri("/teams/{id}/exists", teamId)
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    private boolean teamExistsFallback(Long teamId, Throwable t) {
        throw new ServiceUnavailableException("user-service is currently unavailable, please try again shortly");
    }
}