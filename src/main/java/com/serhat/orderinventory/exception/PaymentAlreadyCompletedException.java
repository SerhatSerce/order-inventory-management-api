package com.serhat.orderinventory.exception;

public class PaymentAlreadyCompletedException extends RuntimeException {

    public PaymentAlreadyCompletedException(Long orderId) {
        super("Payment has already been completed for order id: " + orderId);
    }
}