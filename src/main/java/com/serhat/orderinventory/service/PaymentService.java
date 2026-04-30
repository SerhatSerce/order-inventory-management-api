package com.serhat.orderinventory.service;

import com.serhat.orderinventory.dto.payment.PaymentRequest;
import com.serhat.orderinventory.dto.payment.PaymentResponse;
import com.serhat.orderinventory.entity.Order;
import com.serhat.orderinventory.entity.Payment;
import com.serhat.orderinventory.enums.OrderStatus;
import com.serhat.orderinventory.enums.PaymentStatus;
import com.serhat.orderinventory.exception.InvalidOrderStatusException;
import com.serhat.orderinventory.exception.OrderNotFoundException;
import com.serhat.orderinventory.exception.PaymentAlreadyCompletedException;
import com.serhat.orderinventory.exception.PaymentFailedException;
import com.serhat.orderinventory.exception.PaymentNotFoundException;
import com.serhat.orderinventory.repository.OrderRepository;
import com.serhat.orderinventory.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .filter(existingOrder -> !existingOrder.isDeleted())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            log.warn("Payment rejected. orderId={}, currentStatus={}",
                    order.getId(),
                    order.getStatus()
            );

            throw new PaymentAlreadyCompletedException(order.getId());
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(request.getMethod());
        payment.setReferenceNumber(generateReferenceNumber());

        if (request.isSuccessful()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            if (!order.getStatus().canTransitionTo(OrderStatus.PAID)) {
                throw new InvalidOrderStatusException(order.getStatus(), OrderStatus.PAID);
            }

            order.setStatus(OrderStatus.PAID);
            order.setPayment(payment);

            Payment savedPayment = paymentRepository.save(payment);

            log.info("Payment completed successfully. paymentId={}, orderId={}, amount={}, method={}, referenceNumber={}",
                    savedPayment.getId(),
                    order.getId(),
                    savedPayment.getAmount(),
                    savedPayment.getMethod(),
                    savedPayment.getReferenceNumber()
            );

            log.info("Order status changed after payment. orderId={}, newStatus={}",
                    order.getId(),
                    order.getStatus()
            );

            return mapToResponse(savedPayment);
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Simulated payment failure");

        log.warn("Payment failed. orderId={}, amount={}, method={}",
                order.getId(),
                payment.getAmount(),
                payment.getMethod()
        );

        throw new PaymentFailedException(order.getId());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> !payment.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .filter(existingPayment -> !existingPayment.isDeleted())
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return mapToResponse(payment);
    }

    private String generateReferenceNumber() {
        return "PAY-" + UUID.randomUUID();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setOrderId(payment.getOrder().getId());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setReferenceNumber(payment.getReferenceNumber());
        response.setPaidAt(payment.getPaidAt());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());

        return response;
    }
}