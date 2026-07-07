package com.product_read_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_read_service.document.ProductDocument;
import com.product_read_service.dto.ProductRequest;
import com.product_read_service.dto.ProductResponse;
import com.product_read_service.dto.StandardResponse;
import com.product_read_service.service.ProductQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/poducts/queries")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductResponse>> getProductById(@PathVariable String id) {
        ProductResponse document = productQueryService.getProductById(id);

        StandardResponse<ProductResponse> response = StandardResponse.<ProductResponse>builder()
                .success(true)
                .message("Product retrieved successfully from search index.")
                .data(document)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<StandardResponse<List<ProductResponse>>> findProducts(
            @RequestParam("keyword") String keyword) {
        List<ProductResponse> listings = productQueryService.searchProductsByKeyword(keyword);

        StandardResponse<List<ProductResponse>> response = StandardResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Search results compiled from Elasticsearch database.")
                .data(listings)
                .build();

        return ResponseEntity.ok(response);
    }
}
