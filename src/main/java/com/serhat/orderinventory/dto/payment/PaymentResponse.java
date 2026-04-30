package com.serhat.orderinventory.dto.payment;

import com.serhat.orderinventory.enums.PaymentMethod;
import com.serhat.orderinventory.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long id;

    private Long orderId;

    private BigDecimal amount;

    private PaymentMethod method;

    private PaymentStatus status;

    private String referenceNumber;

    private LocalDateTime paidAt;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}