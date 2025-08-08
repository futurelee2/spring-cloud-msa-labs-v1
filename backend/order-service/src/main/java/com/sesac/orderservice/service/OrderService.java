package com.sesac.orderservice.service;

import com.sesac.orderservice.dto.OrderRequestDto;
import com.sesac.orderservice.entity.Order;
import com.sesac.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order not found with id: " + id)
        );
    }

    //주문 생성 (고객이 주문했을 때)
    //유저 정보
    //상품 정보
    public void createOrder(OrderRequestDto request) {
    }

}
