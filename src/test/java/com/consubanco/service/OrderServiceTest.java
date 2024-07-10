package com.consubanco.service;

import com.consubanco.model.Order;
import com.consubanco.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks private OrderService orderService;
    @Mock private OrderRepository orderRepository;
    @Mock private PaymentService paymentService;

    @Test
    void whenPlaceOrderWasSuccess() {
        when(paymentService.processPayment(anyDouble())).thenReturn(true);
        doNothing().when(orderRepository).save(any());

        var result = orderService.placeOrder(new Order());
        verify(orderRepository, times(1)).save(any());
        Assertions.assertTrue(result);
    }

    @Test
    void whenPlaceOrderWasNotSuccess() {
        when(paymentService.processPayment(anyDouble())).thenReturn(false);
        var result = orderService.placeOrder(new Order());
        verify(orderRepository, times(0)).save(any());
        Assertions.assertFalse(result);
    }

    @Test
    void whenExistsOneOrder() {
        Order order = new Order();
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));
        var result = orderService.getOrderById(1);
        Assertions.assertEquals(result, order);
    }

    @Test
    void whenNotExistsOrders() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());
        var result = orderService.getOrderById(1);
        Assertions.assertNull(result);
    }

    @Test
    void whenOrderWasCancelled() {
        Order order = new Order();
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(any());
        orderService.cancelOrder(1);
        verify(orderRepository, times(1)).delete(any());
    }

    @Test
    void throwExceptionWhenOrderNotExists() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.cancelOrder(1);
        });
        Assertions.assertEquals(exception.getMessage(),"Order not found");
    }

    @Test
    void listAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));
        Assertions.assertNotNull(orderService.listAllOrders());
        Assertions.assertEquals(orderService.listAllOrders().size(), 1);
    }
}