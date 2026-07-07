package com.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.payment_service.dto.InventoryResponse;
import com.payment_service.dto.StandardResponse;

@FeignClient(name = "inventory-service", path = "/api/v1/inventorys")
public interface InventoryClient {

    @GetMapping("/verify")
    ResponseEntity<StandardResponse<InventoryResponse>> checkStock(
            @RequestParam("productId") Long productId, 
            @RequestParam("quantity") Integer quantity
    );
}


