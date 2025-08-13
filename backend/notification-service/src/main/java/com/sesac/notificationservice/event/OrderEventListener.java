package com.sesac.notificationservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener { // 이벤트 수신

    @RabbitListener(queues = "${order.event.queue.notification}") // dev.yml 에 선언함
    public void handlerOrderEvent(OrderCreatedEvent event){ // 큐에 있으면 알아서 실행됨
        log.info("주문 생성 이벤트 수신 - orderId : {}",event.getOrderId());

        try{
            // 알림 보내기 - 이메일/카톡 알림으로 교체 가능
            Thread.sleep(3000);
            log.info("이메일 발송 완료 - orderId : {}",event.getOrderId());
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("이메일 발송 실패 - orderId : {}",event.getOrderId());

        }
    }
}
