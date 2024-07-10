package com.consubanco.service;

import com.consubanco.model.Order;
import com.consubanco.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public boolean placeOrder(Order order) {
        boolean paymentProcessed = paymentService.processPayment(order.getAmount());
        if (paymentProcessed) {
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElse(null);
    }

    public void cancelOrder(int id) {
        orderRepository.findById(id)
            .ifPresentOrElse(orderRepository::delete,
                    () -> {
                    throw new IllegalArgumentException("Order not found");
            });
    }
    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }
}
