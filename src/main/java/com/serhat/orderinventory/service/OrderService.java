package com.serhat.orderinventory.service;

import com.serhat.orderinventory.dto.order.CreateOrderItemRequest;
import com.serhat.orderinventory.dto.order.CreateOrderRequest;
import com.serhat.orderinventory.dto.order.OrderItemResponse;
import com.serhat.orderinventory.dto.order.OrderResponse;
import com.serhat.orderinventory.entity.Customer;
import com.serhat.orderinventory.entity.Order;
import com.serhat.orderinventory.entity.OrderItem;
import com.serhat.orderinventory.entity.Product;
import com.serhat.orderinventory.enums.OrderStatus;
import com.serhat.orderinventory.exception.CustomerNotFoundException;
import com.serhat.orderinventory.exception.InsufficientStockException;
import com.serhat.orderinventory.exception.InvalidOrderStatusException;
import com.serhat.orderinventory.exception.OrderNotFoundException;
import com.serhat.orderinventory.exception.ProductNotFoundException;
import com.serhat.orderinventory.repository.CustomerRepository;
import com.serhat.orderinventory.repository.OrderRepository;
import com.serhat.orderinventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .filter(existingCustomer -> !existingCustomer.isDeleted())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .filter(existingProduct -> !existingProduct.isDeleted())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                log.warn("Insufficient stock. productId={}, productName={}, requestedQuantity={}, availableQuantity={}",
                        product.getId(),
                        product.getName(),
                        itemRequest.getQuantity(),
                        product.getStockQuantity()
                );

                throw new InsufficientStockException(
                        product.getName(),
                        itemRequest.getQuantity(),
                        product.getStockQuantity()
                );
            }

            Integer oldStock = product.getStockQuantity();
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());

            log.info("Stock decreased for order creation. productId={}, oldStock={}, quantity={}, newStock={}",
                    product.getId(),
                    oldStock,
                    itemRequest.getQuantity(),
                    product.getStockQuantity()
            );

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setLineTotal(lineTotal);

            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        log.info("Order created. orderId={}, customerId={}, status={}, totalAmount={}",
                savedOrder.getId(),
                customer.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount()
        );

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderStatus status, Long customerId, Pageable pageable) {
        Page<Order> orders;

        if (status != null && customerId != null) {
            orders = orderRepository.findByStatusAndCustomer_IdAndDeletedFalse(status, customerId, pageable);
        } else if (status != null) {
            orders = orderRepository.findByStatusAndDeletedFalse(status, pageable);
        } else if (customerId != null) {
            orders = orderRepository.findByCustomer_IdAndDeletedFalse(customerId, pageable);
        } else {
            orders = orderRepository.findByDeletedFalse(pageable);
        }

        return orders.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderById(id);

        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            log.warn("Invalid order cancellation attempt. orderId={}, currentStatus={}",
                    order.getId(),
                    order.getStatus()
            );

            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled from status: " + order.getStatus()
            );
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            Integer oldStock = product.getStockQuantity();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());

            log.info("Stock restored after order cancellation. orderId={}, productId={}, oldStock={}, restoredQuantity={}, newStock={}",
                    order.getId(),
                    product.getId(),
                    oldStock,
                    item.getQuantity(),
                    product.getStockQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.save(order);

        log.info("Order cancelled. orderId={}, status={}",
                savedOrder.getId(),
                savedOrder.getStatus()
        );

        return mapToResponse(savedOrder);
    }

    @Transactional
    public OrderResponse shipOrder(Long id) {
        Order order = findOrderById(id);

        if (!order.getStatus().canTransitionTo(OrderStatus.SHIPPED)) {
            log.warn("Invalid order shipment attempt. orderId={}, currentStatus={}",
                    order.getId(),
                    order.getStatus()
            );

            throw new InvalidOrderStatusException(order.getStatus(), OrderStatus.SHIPPED);
        }

        order.setStatus(OrderStatus.SHIPPED);

        Order savedOrder = orderRepository.save(order);

        log.info("Order shipped. orderId={}, status={}",
                savedOrder.getId(),
                savedOrder.getStatus()
        );

        return mapToResponse(savedOrder);
    }

    @Transactional
    public OrderResponse deliverOrder(Long id) {
        Order order = findOrderById(id);

        if (!order.getStatus().canTransitionTo(OrderStatus.DELIVERED)) {
            log.warn("Invalid order delivery attempt. orderId={}, currentStatus={}",
                    order.getId(),
                    order.getStatus()
            );

            throw new InvalidOrderStatusException(order.getStatus(), OrderStatus.DELIVERED);
        }

        order.setStatus(OrderStatus.DELIVERED);

        Order savedOrder = orderRepository.save(order);

        log.info("Order delivered. orderId={}, status={}",
                savedOrder.getId(),
                savedOrder.getStatus()
        );

        return mapToResponse(savedOrder);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .filter(existingOrder -> !existingOrder.isDeleted())
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getFullName());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(this::mapItemToResponse)
                .toList();

        response.setItems(itemResponses);

        return response;
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();

        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setLineTotal(item.getLineTotal());

        return response;
    }
}