package com.sesac.productservice.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 주문생성 했을 때 이벤트(메세지) 발행
// 주문이 되었을 때, 주문 정보 담기
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable { //직렬화할때 필요 (이벤트를 네트워크로 보낼때 string 처리)
    // 메세지
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

}
