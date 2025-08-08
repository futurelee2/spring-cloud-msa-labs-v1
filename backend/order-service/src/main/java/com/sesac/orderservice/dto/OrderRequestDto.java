package com.sesac.orderservice.dto;

import lombok.Data;

// 단 건 주문조회
@Data
public class OrderRequestDto {
    private Long productId;
    private Long userId;
    private Integer quantity;

}
