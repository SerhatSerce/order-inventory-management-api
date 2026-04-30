package com.serhat.orderinventory.exception;

import com.serhat.orderinventory.enums.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(OrderStatus currentStatus, OrderStatus newStatus) {
        super("Invalid order status transition from " + currentStatus + " to " + newStatus);
    }

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}