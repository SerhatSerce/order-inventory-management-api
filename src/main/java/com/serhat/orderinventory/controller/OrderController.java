package com.serhat.orderinventory.controller;

import com.serhat.orderinventory.dto.order.CreateOrderRequest;
import com.serhat.orderinventory.dto.order.OrderResponse;
import com.serhat.orderinventory.enums.OrderStatus;
import com.serhat.orderinventory.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public Page<OrderResponse> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long customerId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return orderService.getAllOrders(status, customerId, pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PostMapping("/{id}/ship")
    public OrderResponse shipOrder(@PathVariable Long id) {
        return orderService.shipOrder(id);
    }

    @PostMapping("/{id}/deliver")
    public OrderResponse deliverOrder(@PathVariable Long id) {
        return orderService.deliverOrder(id);
    }
}