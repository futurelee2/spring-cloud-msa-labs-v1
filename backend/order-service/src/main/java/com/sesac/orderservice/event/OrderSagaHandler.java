package com.sesac.orderservice.event;

import com.sesac.orderservice.entity.Order;
import com.sesac.orderservice.entity.OrderStatus;
import com.sesac.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaHandler {

    private final OrderRepository orderRepository;


    // 수신 이벤트 목록
    // InventoryFailedEvent

    //PaymentCompletedEvent

    //PaymentFailedEvent

    @RabbitListener(queues = "${order.event.queue.inventory-failed}")
    @Transactional
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.info("재고 실패 이벤트 수신 - orderId:{}, reason: {} ",event.getOrderId(), event.getReason());
        try{
            Order order = orderRepository.findById(event.getOrderId())
                                         .orElseThrow(()-> new RuntimeException("주문을 찾을 수 없음" + event.getOrderId()));

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

        } catch (Exception e){
            log.error("재고 실패 처리 중 오류 - orderId : {} , error: {}",event.getOrderId(), e.getMessage());
        }


    }
    @RabbitListener(queues = "${order.event.queue.payment-completed}")
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("결제 완료 이벤트 수신 - orderId:{}, amount: {} ",event.getOrderId(), event.getAmount());
        // 주문 상태 -> completed 수정
        try{
            Order order = orderRepository.findById(event.getOrderId())
                                         .orElseThrow(()-> new RuntimeException("주문을 찾을 수 없음" + event.getOrderId()));

            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

        } catch (Exception e){
            log.error("결제 완료 처리 중 오류 - orderId : {} , error: {}",event.getOrderId(), e.getMessage());
        }

    }


    @RabbitListener(queues = "${order.event.queue.payment-failed}")
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        try{
            Order order = orderRepository.findById(event.getOrderId())
                                         .orElseThrow(()-> new RuntimeException("주문을 찾을 수 없음" + event.getOrderId()));

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

        } catch (Exception e){
            log.error("결제 실패 처리 중 오류 - orderId : {} , error: {}",event.getOrderId(), e.getMessage());
        }

    }

}
