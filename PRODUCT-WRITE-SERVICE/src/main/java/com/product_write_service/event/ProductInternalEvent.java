package com.product_write_service.event;

import org.springframework.context.ApplicationEvent;

import com.common.avro.schemas.AvroProductSyncEvent;

import lombok.Getter;

@Getter
public class ProductInternalEvent extends ApplicationEvent {

    private final String productId;
    private final String action;
    private final AvroProductSyncEvent avroProductSyncEvent;

    public ProductInternalEvent(Object source, String productId, String action,
            AvroProductSyncEvent avroProductSyncEvent) {
        super(source);
        this.productId = productId;
        this.action = action;
        this.avroProductSyncEvent = avroProductSyncEvent;
    }

}
