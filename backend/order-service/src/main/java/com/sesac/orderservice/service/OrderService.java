package com.sesac.orderservice.service;

import com.sesac.orderservice.client.ProductServiceClient;
import com.sesac.orderservice.client.UserServiceClient;
import com.sesac.orderservice.client.dto.ProductDto;
import com.sesac.orderservice.client.dto.UserDto;
import com.sesac.orderservice.dto.OrderRequestDto;
import com.sesac.orderservice.entity.Order;
import com.sesac.orderservice.event.OrderCreatedEvent;
import com.sesac.orderservice.event.OrderEventPublisher;
import com.sesac.orderservice.facade.ProductServiceFacade;
import com.sesac.orderservice.facade.UserServiceFacade;
import com.sesac.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    //private final UserServiceClient userServiceClient; // Openfeign 추상화 클라이언트
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;
//    private final ProductServiceFacade productServiceFacade;
    private final UserServiceFacade userServiceFacade;
    private final Tracer tracer;
    private final OrderEventPublisher orderEventPublisher;


    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order not found with id: " + id)
        );
    }

    //주문 생성 (고객이 주문했을 때)
    @Transactional
    public Order createOrder(OrderRequestDto request) {

        // span 생성
        Span span = tracer.nextSpan()
                .name("createOrder")
                .tag("order.userId", request.getUserId())
                .tag("order.productId", request.getProductId())
                .start();
        // span 생성을 위해 기존에 작성한 내용 try 문 안에 넣기
        try(Tracer.SpanInScope ws = tracer.withSpan(span)) { // => span 활성화 시키기

            // 주문했을 때 order 만드는게 목적
            UserDto user = userServiceFacade.getUserWithFallback(request.getUserId()); // getUserById 호출할때 서킷브레이커 걸어주기
//        UserDto user = userServiceClient.getUserById(request.getUserId()); // getUserById 호출할때 서킷브레이커 걸어주기
            if (user == null) throw new RuntimeException("User not found with id: " + request.getUserId());

//            ProductDto product =  productServiceFacade.getProductById(request.getProductId(), request.getQuantity());
        ProductDto product =  productServiceClient.getProductById(request.getProductId());
            if (product == null) throw new RuntimeException("Product not found with id: " + request.getProductId());

//            if (product.getStockQuantity() < request.getQuantity()){
//                throw new RuntimeException("Stock quantity less than requested quantity: " + request.getQuantity());
//            }

            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            order.setStatus("COMPLETED");

            // 비동기 이벤트 발행
            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId(),
                    request.getUserId(),
                    request.getProductId(),
                    request.getQuantity(),
                    order.getTotalAmount(),
                    LocalDateTime.now()
            );
            orderEventPublisher.publishOrderCreated(event);


            return orderRepository.save(order);

        } catch (Exception e){
            span.tag("error", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }


    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }


}
