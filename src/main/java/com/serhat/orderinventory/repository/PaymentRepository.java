package com.serhat.orderinventory.repository;

import com.serhat.orderinventory.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}