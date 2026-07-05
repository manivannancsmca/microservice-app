package com.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class DynamicRateLimiterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<DynamicRateLimiterGatewayFilterFactory.Config> {

    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver keyResolver;

    // Inject the native framework components directly
    public DynamicRateLimiterGatewayFilterFactory(RedisRateLimiter redisRateLimiter, KeyResolver keyResolver) {
        super(Config.class);
        this.redisRateLimiter = redisRateLimiter;
        this.keyResolver = keyResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Removed the startup/refresh print statement from here to stop the background logging noise.

        return (exchange, chain) -> keyResolver.resolve(exchange).flatMap(key -> {

            // This only runs when an actual HTTP request hits your API Gateway
            System.out.println(">>> Processing Rate Limiter filter for Key: " + key + " on route: " + config.getRouteId());

            // Safely parse and lock the tier string into a final variable immediately
            String rawTier = exchange.getRequest().getHeaders().getFirst("X-User-Tier");
            final String tier = (rawTier == null || rawTier.isBlank()) ? "NORMAL" : rawTier.toUpperCase();

            String baseRouteId = config.getRouteId() != null ? config.getRouteId() : "default_route";
            String dynamicRouteKey = baseRouteId + "-" + tier;

            // Compute rate limiting rules dynamically based on tier
            redisRateLimiter.getConfig().computeIfAbsent(dynamicRouteKey, k -> {
                RedisRateLimiter.Config rateConfig = new RedisRateLimiter.Config();
                if ("PREMIUM".equals(tier)) {
                    rateConfig.setReplenishRate(1);
                    rateConfig.setBurstCapacity(30);
                } else {
                    rateConfig.setReplenishRate(1);
                    rateConfig.setBurstCapacity(10);
                }
                rateConfig.setRequestedTokens(1);
                return rateConfig;
            });

            return redisRateLimiter.isAllowed(dynamicRouteKey, key).flatMap(response -> {
                System.out.println(">>> Redis Rate Limit Check for key [" + key + "] - Allowed: " + response.isAllowed());

                // Inject tracking metadata headers into response stream
                response.getHeaders().forEach((headerName, headerValue) -> {
                    exchange.getResponse().getHeaders().add(headerName, headerValue);
                });

                if (response.isAllowed()) {
                    return chain.filter(exchange);
                }

                // Explicitly return completion pipeline signal on rate limit exhaustion
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            });
        });
    }

    public static class Config {
        private String routeId;

        public String getRouteId() {
            return routeId;
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }
    }
}