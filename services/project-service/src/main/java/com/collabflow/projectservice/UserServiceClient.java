package com.collabflow.projectservice;

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

    public boolean teamExists(Long teamId) {
        try {
            Boolean result = restClient.get()
                    .uri("/teams/{id}/exists", teamId)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}
