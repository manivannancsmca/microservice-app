package com.product_read_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.product_read_service.document.ProductDocument;
import com.product_read_service.dto.ProductRequest;
import com.product_read_service.dto.ProductResponse;
import com.product_read_service.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductSearchRepository searchRepository;

    public List<ProductResponse> searchProductsByKeyword(String keyword) {
        List<ProductDocument> documents = searchRepository.findByNameContainingOrDescriptionContaining(keyword,
                keyword);

        return documents.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public ProductResponse getProductById(String id) {

        ProductDocument document = searchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        return convertToResponse(document);
    }


    private ProductResponse convertToResponse(ProductDocument document) {

        return ProductResponse.builder()
                .id(Long.valueOf(document.getId()))
                .name(document.getName())
                .description(document.getDescription())
                .price(document.getPrice())
                .skuCode(document.getSkuCode())
                .build();
    }

}
