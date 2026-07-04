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
        // Print statement to confirm the gateway factory is mapping properly at start
        System.out.println(">>> DynamicRateLimiter applied for route ID: " + config.getRouteId());

        return (exchange, chain) -> keyResolver.resolve(exchange).flatMap(key -> {

            // Print statement when a request successfully passes through the filter
            // execution block
            System.out.println(">>> Processing Rate Limiter filter for Key: " + key);

            // FIX: Safely parse and lock the tier string into a final variable immediately
            String rawTier = exchange.getRequest().getHeaders().getFirst("X-User-Tier");
            final String tier = (rawTier == null || rawTier.isBlank()) ? "NORMAL" : rawTier.toUpperCase();

            String baseRouteId = config.getRouteId() != null ? config.getRouteId() : "default_route";
            String dynamicRouteKey = baseRouteId + "-" + tier;

            // 'tier' is now final, so the lambda compiler is happy
            redisRateLimiter.getConfig().computeIfAbsent(dynamicRouteKey, k -> {
                RedisRateLimiter.Config rateConfig = new RedisRateLimiter.Config();
                if ("PREMIUM".equals(tier)) {
                    // Example: 30 requests per minute
                    // 60 seconds / 30 requests = 1 token replenished every 2 seconds
                    rateConfig.setReplenishRate(1);
                    rateConfig.setBurstCapacity(30);
                } else {
                    // STANDARD: 10 requests per minute
                    // 60 seconds / 10 requests = 1 token replenished every 6 seconds
                    rateConfig.setReplenishRate(1);
                    rateConfig.setBurstCapacity(10);
                }
                rateConfig.setRequestedTokens(1);
                return rateConfig;
            });

            return redisRateLimiter.isAllowed(dynamicRouteKey, key).flatMap(response -> {
                System.out.println(">>> Redis Rate Limit Check - Allowed: " + response.isAllowed());

                // Always inject tracking metadata headers into response stream
                response.getHeaders().forEach((headerName, headerValue) -> {
                    exchange.getResponse().getHeaders().add(headerName, headerValue);
                });

                if (response.isAllowed()) {
                    return chain.filter(exchange);
                }

                // FIX: Set the status code AND explicitly return the completion pipeline signal
                // to prevent downstream routing filters from running!
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