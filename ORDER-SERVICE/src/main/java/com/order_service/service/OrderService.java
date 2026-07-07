package com.order_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.common.avro.schemas.OrderPlacedEvent;
import com.order_service.client.ProductClient;
import com.order_service.client.UserClient;
import com.order_service.dto.OrderRequest;
import com.order_service.dto.OrderResponse;
import com.order_service.dto.ProductResponse;
import com.order_service.dto.StandardResponse;
import com.order_service.dto.UserResponse;
import com.order_service.entity.Order;
import com.order_service.enums.OrderStatus;
import com.order_service.exception.ResourceNotFoundException;
import com.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductClient productClient;

    private final UserClient userClient;

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private static final String TOPIC_NAME = "order-placed-topic";

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        ResponseEntity<StandardResponse<ProductResponse>> feignProductResponse = productClient
                .getProductById(request.getProductId());

        ResponseEntity<StandardResponse<UserResponse>> feignUserResponse = userClient
                .getUserById(request.getUserId());

        if (feignProductResponse.getBody() == null || !feignProductResponse.getBody().isSuccess()
                || feignProductResponse.getBody().getData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product details are invalid or missing.");
        }

        if (feignUserResponse.getBody() == null || !feignUserResponse.getBody().isSuccess()
                || feignUserResponse.getBody().getData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User details are invalid or missing.");
        }

        ProductResponse productDetails = feignProductResponse.getBody().getData();

        UserResponse userDetails = feignUserResponse.getBody().getData();

        BigDecimal totalPrice = productDetails.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .productId(productDetails.getId())
                .userId(userDetails.getId())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // 5. Emit asynchronous event to Kafka broker
        OrderPlacedEvent event = OrderPlacedEvent.newBuilder()
                .setOrderId(savedOrder.getId())
                .setProductId(savedOrder.getProductId())
                .setUserId(savedOrder.getUserId())
                .setQuantity(savedOrder.getQuantity())
                .setTotalAmount(savedOrder.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue())
                .build();
        // log.info("OrderPlacedEvent : {} ", event);
        kafkaTemplate.send(TOPIC_NAME, String.valueOf(savedOrder.getId()), event);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order records not located with tracking ID: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {

        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No orders found for user id : " + userId);
        }

        return orders.map(this::mapToResponse);
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order records not located with tracking ID: " + orderId));

        if (status.equalsIgnoreCase("SUCCESS")) {
            order.setOrderStatus(OrderStatus.COMPLETED);
        } else if (status.equalsIgnoreCase("REJECTED")) {
            order.setOrderStatus(OrderStatus.CANCELLED);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLATION_REQUESTED);
        }

        orderRepository.save(order);

    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .userId(order.getUserId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalPrice())
                .orderStatus(order.getOrderStatus().toString())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
