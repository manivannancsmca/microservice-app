package com.product_read_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_read_service.dto.ProductRequest;
import com.product_read_service.dto.StandardResponse;
import com.product_read_service.service.ProductQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/poducts/queries")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductRequest>> getProductById(@PathVariable String id) {
        ProductRequest document = productQueryService.getProductById(id);
        
        StandardResponse<ProductRequest> response = StandardResponse.<ProductRequest>builder()
                .success(true)
                .message("Product retrieved successfully from search index.")
                .data(document)
                .build();
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<StandardResponse<List<ProductRequest>>> searchProducts(@RequestParam("keyword") String keyword) {
        List<ProductRequest> searchResults = productQueryService.searchProducts(keyword);
        
        StandardResponse<List<ProductRequest>> response = StandardResponse.<List<ProductRequest>>builder()
                .success(true)
                .message("Fuzzy search queries evaluated successfully.")
                .data(searchResults)
                .build();
                
        return ResponseEntity.ok(response);
    }
}
