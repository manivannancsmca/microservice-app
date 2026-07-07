package com.product_write_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductWriteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductWriteServiceApplication.class, args);
	}

}
