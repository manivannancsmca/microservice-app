package com.product_write_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_write_service.entity.ProductCommandEntity;

public interface ProductCommandRepository extends JpaRepository<ProductCommandEntity, String> {

}
