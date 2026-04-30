package com.serhat.orderinventory.repository;

import com.serhat.orderinventory.entity.Order;
import com.serhat.orderinventory.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByDeletedFalse(Pageable pageable);

    Page<Order> findByStatusAndDeletedFalse(OrderStatus status, Pageable pageable);

    Page<Order> findByCustomer_IdAndDeletedFalse(Long customerId, Pageable pageable);

    Page<Order> findByStatusAndCustomer_IdAndDeletedFalse(
            OrderStatus status,
            Long customerId,
            Pageable pageable
    );
}