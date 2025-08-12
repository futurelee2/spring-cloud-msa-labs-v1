package com.sesac.orderservice.facade;

import com.sesac.orderservice.client.ProductServiceClient;
import com.sesac.orderservice.client.dto.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceFacade {
    private final ProductServiceClient productServiceClient;

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductFallback")
    @Retry(name = "product-service")
    public ProductDto getProductById(Long productId, Integer quantity) {
        log.info("Product service 호출 시도 - productId={}",productId);
        return  productServiceClient.getProductById(productId);
    }

    public ProductDto getProductFallback(Long productId, Integer quantity, Throwable ex) {
        log.warn("Product Service 장애감지! Fallback 실행 - productId={}, error ={}",productId, ex.getMessage());
        ProductDto defaultProduct = new ProductDto();
        defaultProduct.setId(productId);
        defaultProduct.setName("임시 상품");
        defaultProduct.setPrice(BigDecimal.valueOf(0));
        defaultProduct.setStockQuantity(quantity);
        return defaultProduct;
    }
}
