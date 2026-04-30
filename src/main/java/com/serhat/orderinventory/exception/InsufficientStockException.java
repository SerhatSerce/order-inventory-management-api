package com.serhat.orderinventory.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, Integer requestedQuantity, Integer availableQuantity) {
        super("Insufficient stock for product: " + productName +
                ". Requested: " + requestedQuantity +
                ", available: " + availableQuantity);
    }
}