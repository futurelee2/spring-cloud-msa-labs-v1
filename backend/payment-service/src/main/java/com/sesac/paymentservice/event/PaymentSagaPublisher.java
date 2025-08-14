package com.sesac.paymentservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentSagaPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${order.event.exchange}")
    private String exchange;
    @Value("${order.event.routing-key.payment-completed}")
    private String paymentCompletedRoutingKey;

    @Value("${order.event.routing-key.payment-failed}")
    private String paymentFailedRoutingKey;

    @Value("${order.event.routing-key.inventory-restore}")
    private String inventoryRestoreRoutingKey;
    

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        rabbitTemplate.convertAndSend(exchange, paymentCompletedRoutingKey, event);

    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        // order-service 결제 실패 이벤트 발행 - 주문 취소해!
        rabbitTemplate.convertAndSend(exchange,paymentFailedRoutingKey, event);
        // product-service 결제 실패 이벤트 발행 - 재고 복구해!
        rabbitTemplate.convertAndSend(exchange,inventoryRestoreRoutingKey, event);
        
    }
}
