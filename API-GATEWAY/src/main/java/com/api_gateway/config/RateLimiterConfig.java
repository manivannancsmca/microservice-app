package com.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Optional;

@Configuration
public class RateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    @Bean
    @Primary
    public KeyResolver userIpKeyResolver() {
        return exchange -> {
            // 1. Check for standard proxy headers (Used in K8s environment)
            String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            
            String resolvedKey;
            if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                // Take the original client IP (first value in chain)
                resolvedKey = xForwardedFor.split(",")[0].trim();
            } else {
                // 2. Fallback to direct Remote Address (Used in Local testing environment)
                resolvedKey = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                        .map(InetSocketAddress::getHostString)
                        .orElse("local-anonymous");
            }
            
            log.debug("Rate limiting evaluated key: [{}]", resolvedKey);
            return Mono.just(resolvedKey);
        };
    }
}