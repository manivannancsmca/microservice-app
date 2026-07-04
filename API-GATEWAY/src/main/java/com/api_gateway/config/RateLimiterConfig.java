package com.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.InetSocketAddress;
import java.util.Optional;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userIdOrIpKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                return Mono.just("user_" + userId);
            }

            String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            String ipAddress;
            if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                ipAddress = xForwardedFor.split(",")[0].trim();
            } else {
                ipAddress = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                        .map(InetSocketAddress::getHostString)
                        .orElse("anonymous");
            }
            return Mono.just("ip_" + ipAddress);
        };
    }
}