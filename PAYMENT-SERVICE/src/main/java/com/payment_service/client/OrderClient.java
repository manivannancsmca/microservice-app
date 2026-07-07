package com.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.payment_service.dto.OrderResponse;
import com.payment_service.dto.StandardResponse;

import org.springframework.http.ResponseEntity;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderClient {

    @GetMapping("/{id}")
    ResponseEntity<StandardResponse<OrderResponse>> getOrderById(@PathVariable("id") Long id);
}
