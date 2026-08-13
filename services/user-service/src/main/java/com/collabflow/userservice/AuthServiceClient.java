package com.collabflow.userservice;
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

    public boolean userExists(Long userId) {
        try {
            Boolean result = restClient.get()
                    .uri("/auth/users/{id}/exists", userId)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}
