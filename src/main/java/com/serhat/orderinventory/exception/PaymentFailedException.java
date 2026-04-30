package com.serhat.orderinventory.exception;

public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(Long orderId) {
        super("Payment failed for order id: " + orderId);
    }
}