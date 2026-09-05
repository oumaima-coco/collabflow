package com.collabflow.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;

import static org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class RouteConfig {

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-service")
                .route(path("/auth/**"), http())
                .filter(lb("AUTH-SERVICE"))
                .filter(rateLimit(c -> c.setCapacity(100)
                        .setPeriod(Duration.ofMinutes(1))
                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .route(path("/teams/**"), http())
                .filter(lb("USER-SERVICE"))
                .filter(rateLimit(c -> c.setCapacity(100)
                        .setPeriod(Duration.ofMinutes(1))
                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> projectServiceRoute() {
        return route("project-service")
                .route(path("/projects/**"), http())
                .filter(lb("PROJECT-SERVICE"))
                .filter(rateLimit(c -> c.setCapacity(100)
                        .setPeriod(Duration.ofMinutes(1))
                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute() {
        return route("notification-service")
                .route(path("/notifications/**"), http())
                .filter(lb("NOTIFICATION-SERVICE"))
                .filter(rateLimit(c -> c.setCapacity(100)
                        .setPeriod(Duration.ofMinutes(1))
                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> activityServiceRoute() {
        return route("activity-service")
                .route(path("/activity/**"), http())
                .filter(lb("ACTIVITY-SERVICE"))
                .filter(rateLimit(c -> c.setCapacity(100)
                        .setPeriod(Duration.ofMinutes(1))
                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr())))
                .build();
    }
}