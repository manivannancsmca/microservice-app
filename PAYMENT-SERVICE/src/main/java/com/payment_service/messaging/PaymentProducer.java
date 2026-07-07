package com.payment_service.messaging;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;

import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.PaymentCompletedEvent;
import com.payment_service.entity.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    public void paymentSuccessEvent(Payment payment) {

        PaymentCompletedEvent event = paymentDetails(payment);

        kafkaTemplate.send(
                "payment-completed-events",
                String.valueOf(payment.getId()),
                event);
    }

    public void paymentFailedEvent(Payment payment) {
         PaymentCompletedEvent event = paymentDetails(payment);
          kafkaTemplate.send(
                "payment-failed-events",
                String.valueOf(payment.getId()),
                event);
    }

    public PaymentCompletedEvent paymentDetails(Payment payment) {

        Conversions.DecimalConversion conversion = new Conversions.DecimalConversion();

        LogicalTypes.Decimal decimalType = LogicalTypes.decimal(10, 2);

        Schema schema = Schema.create(Schema.Type.BYTES);
        decimalType.addToSchema(schema);

        ByteBuffer buffer = conversion.toBytes(
                payment.getAmount(),
                schema,
                decimalType);

        PaymentCompletedEvent event = PaymentCompletedEvent.newBuilder()
                .setPaymentId(payment.getId())
                .setOrderId(payment.getOrderId())
                .setAmountPaid(buffer)
                .setPaymentStatus(payment.getPaymentStatus().name())
                .setCompletedAt(Instant.now())
                .build();
        return event;

    }

}
