package com.serhat.orderinventory.dto.payment;

import com.serhat.orderinventory.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Order id cannot be null")
    private Long orderId;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod method;

    private boolean successful = true;
}