package com.sesac.productservice.event;

import com.sesac.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSagaHandler { // 이벤트 수신

    private final ProductService productService;
    private final ProductSagaPublisher productSagaPublisher;

    @RabbitListener(queues = "${order.event.queue.inventory}") // dev.yml 에 선언함
    public void handlerOrderEvent(OrderCreatedEvent event){ // 큐에 있으면 알아서 실행됨
        log.info("주문 생성 이벤트 수신 - orderId : {}",event.getOrderId());

        try{
            productService.decreaseStock(event.getProductId(), event.getQuantity());
            // 성공 시 : 결제 요청 이벤트 발행
            PaymentRequestEvent paymentRequestEvent = new PaymentRequestEvent(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getProductId(),
                    event.getQuantity(),
                    event.getTotalAmount()
            );
            productSagaPublisher.publishPaymentRequest(paymentRequestEvent);


        } catch (Exception e) {
            log.error("재고 차감 실패 - productId : {}",event.getProductId());

            // 실패 시 : 재고 차감 실패 이벤트 발행
            InventoryFailedEvent inventoryFailedEvent = new InventoryFailedEvent(
                    event.getOrderId(),
                    event.getProductId(), 
                    event.getQuantity(),
                    "재고 부족"
            );
            productSagaPublisher.publishInventoryFailed(inventoryFailedEvent);

        }
    }

    @RabbitListener(queues = "${order.event.queue.inventory-restore}" )
    public void handlePaymentFailed(PaymentFailedEvent event){
        log.error("결제 실패 이벤트 수신 - orderId : {}, reason : {}",event.getOrderId(),event.getReason());

        try{
            productService.restoreStock(event.getProductId(), event.getQuantity());
            log.error("재고 복구 완료 (보상 트랜젝션) - orderId:{}, productId : {}, quantity: {}", event.getOrderId(), event.getProductId(), event.getQuantity());

        } catch (Exception e) {
            log.error("재고 복구 실패 - orderId:{} , error:{}", event.getOrderId(), e.getMessage());
        }


    }

}
