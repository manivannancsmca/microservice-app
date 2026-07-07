package com.product_read_service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.product_read_service.document.ProductDocument;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    // Fuzzy search matching text across both product titles and descriptions
    List<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description);
}
