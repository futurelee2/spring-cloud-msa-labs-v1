package com.sesac.productservice.event;

import com.sesac.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener { // 이벤트 수신

    private final ProductService productService;

    @RabbitListener(queues = "${order.event.queue.inventory}") // dev.yml 에 선언함
    public void handlerOrderEvent(OrderCreatedEvent event){ // 큐에 있으면 알아서 실행됨
        log.info("주문 생성 이벤트 수신 - orderId : {}",event.getOrderId());

        try{
            productService.decreaseStock(event.getProductId(), event.getQuantity());
        } catch (Exception e) {
            log.error("재고 차감 실패 - productId : {}",event.getProductId());
        }
    }
}
