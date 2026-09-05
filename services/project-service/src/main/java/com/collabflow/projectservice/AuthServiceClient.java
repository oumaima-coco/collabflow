package com.collabflow.projectservice;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthServiceClient {
    private final RestClient restClient;

    public AuthServiceClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://AUTH-SERVICE")
                .build();
    }

    @CircuitBreaker(name = "authService", fallbackMethod = "userExistsFallback")
    @Retry(name = "authService")
    public boolean userExists(Long userId) {
        Boolean result = restClient.get()
                .uri("/auth/users/{id}/exists", userId)
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    private boolean userExistsFallback(Long userId, Throwable t) {
        throw new ServiceUnavailableException("auth-service is currently unavailable, please try again shortly");
    }
}