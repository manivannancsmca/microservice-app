package com.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.order_service.dto.StandardResponse;
import com.order_service.dto.UserResponse;

import org.springframework.http.ResponseEntity;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserClient {

    @GetMapping("/{id}")
    ResponseEntity<StandardResponse<UserResponse>> getUserById(@PathVariable("id") Long id);

}
