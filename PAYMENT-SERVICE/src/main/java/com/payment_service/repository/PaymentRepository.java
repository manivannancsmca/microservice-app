package com.payment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment_service.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);
}
