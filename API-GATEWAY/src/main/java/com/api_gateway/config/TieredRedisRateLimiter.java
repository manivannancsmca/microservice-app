package com.api_gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@Primary
public class TieredRedisRateLimiter {//} extends RedisRateLimiter {

    // private final ReactiveStringRedisTemplate redisTemplate;
    // private final RedisScript<List<Long>> script;

    // @SuppressWarnings("unchecked")
    // public TieredRedisRateLimiter(
    //         ReactiveStringRedisTemplate redisTemplate, 
    //         @Qualifier("redisRateLimiterScript") RedisScript<?> redisScript) { // <-- Added explicit Qualifier mapping
    //     super(3, 3);
    //     this.redisTemplate = redisTemplate;
    //     this.script = (RedisScript<List<Long>>) redisScript;
    // }

    // public Mono<Response> isAllowedCustom(String routeId, String id, ServerWebExchange exchange) {
    //     int tempReplenishRate = 3;
    //     int tempBurstCapacity = 3;

    //     String userTier = exchange.getRequest().getHeaders().getFirst("X-User-Tier");
    //     if ("PREMIUM".equalsIgnoreCase(userTier)) {
    //         tempReplenishRate = 5;
    //         tempBurstCapacity = 5;
    //     }

    //     final int replenishRate = tempReplenishRate;
    //     final int burstCapacity = tempBurstCapacity;

    //     Config dynamicConfig = getConfig().getOrDefault(routeId, new Config());
    //     dynamicConfig.setReplenishRate(replenishRate);
    //     dynamicConfig.setBurstCapacity(burstCapacity);
    //     dynamicConfig.setRequestedTokens(1);
    //     getConfig().put(routeId, dynamicConfig);

    //     List<String> keys = Arrays.asList("request_rate_limiter.{" + id + "}.tokens",
    //             "request_rate_limiter.{" + id + "}.timestamp");

    //     long now = System.currentTimeMillis() / 1000;
    //     List<String> args = Arrays.asList(
    //             String.valueOf(replenishRate),
    //             String.valueOf(burstCapacity),
    //             String.valueOf(now),
    //             "1"
    //     );

    //     return redisTemplate.execute(script, keys, args)
    //             .next()
    //             .map(results -> {
    //                 // Safety check if Redis script returned empty metadata arrays
    //                 if (results == null || results.isEmpty()) {
    //                     return new Response(true, new java.util.HashMap<>());
    //                 }
                    
    //                 boolean allowed = results.get(0) == 1L;
    //                 Long tokensLeft = results.get(1);

    //                 java.util.Map<String, String> headers = new java.util.HashMap<>();
    //                 headers.put(RedisRateLimiter.REMAINING_HEADER, String.valueOf(tokensLeft));
    //                 headers.put(RedisRateLimiter.REPLENISH_RATE_HEADER, String.valueOf(replenishRate));
    //                 headers.put(RedisRateLimiter.BURST_CAPACITY_HEADER, String.valueOf(burstCapacity));

    //                 return new Response(allowed, headers);
    //             })
    //             .onErrorResume(throwable -> {
    //                 // Log error to console if something breaks behind the scenes
    //                 System.err.println("Rate limiter processing error: " + throwable.getMessage());
    //                 return Mono.just(new Response(true, new java.util.HashMap<>()));
    //             });
    // }
}