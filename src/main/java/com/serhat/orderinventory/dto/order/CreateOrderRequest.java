package com.serhat.orderinventory.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "Customer id cannot be null")
    private Long customerId;

    @Valid
    @NotEmpty(message = "Order items cannot be empty")
    private List<CreateOrderItemRequest> items;
}