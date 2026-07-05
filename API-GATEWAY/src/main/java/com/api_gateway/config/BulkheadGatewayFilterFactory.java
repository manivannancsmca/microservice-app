package com.api_gateway.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BulkheadGatewayFilterFactory 
        extends AbstractGatewayFilterFactory<BulkheadGatewayFilterFactory.Config> {

    private final ConcurrentHashMap<String, Bulkhead> bulkheads = new ConcurrentHashMap<>();

    public BulkheadGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String name = config.getName() != null ? config.getName() : "defaultBulkhead";

            // கன்கரண்ட் மேப்பில் பல்க்ஹெட்டை டைனமிக்காக உருவாக்குகிறது
            Bulkhead bulkhead = bulkheads.computeIfAbsent(name, k -> {
                BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
                        .maxConcurrentCalls(config.getMaxConcurrentCalls())
                        .maxWaitDuration(Duration.ofMillis(config.getMaxWaitDuration()))
                        .build();
                return Bulkhead.of(name, bulkheadConfig);
            });

            // த்ரெட் லிமிட்டை செக் செய்கிறது
            if (bulkhead.tryAcquirePermission()) {
                return chain.filter(exchange)
                        .doFinally(signalType -> bulkhead.onComplete()); // ரிக்வெஸ்ட் முடிந்ததும் த்ரெட்டை ரிலீஸ் செய்யும்
            } else {
                // லிமிட் தாண்டிவிட்டால் நேரடியாக ஃபால்பேக் (503) அனுப்பும்
                if (config.getFallbackUri() != null) {
                    return exchange.getPrincipal().flatMap(p -> chain.filter(exchange))
                            .onErrorResume(e -> chain.filter(exchange))
                            .then(Mono.defer(() -> {
                                exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                                return exchange.getApplicationContext().getBean(org.springframework.web.reactive.DispatcherHandler.class)
                                        .handle(exchange);
                            }));
                }
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        private String name;
        private int maxConcurrentCalls = 20; // Default
        private int maxWaitDuration = 20;    // Default ms
        private String fallbackUri;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getMaxConcurrentCalls() { return maxConcurrentCalls; }
        public void setMaxConcurrentCalls(int maxConcurrentCalls) { this.maxConcurrentCalls = maxConcurrentCalls; }
        public int getMaxWaitDuration() { return maxWaitDuration; }
        public void setMaxWaitDuration(int maxWaitDuration) { this.maxWaitDuration = maxWaitDuration; }
        public String getFallbackUri() { return fallbackUri; }
        public void setFallbackUri(String fallbackUri) { this.fallbackUri = fallbackUri; }
    }
}