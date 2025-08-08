package com.sesac.orderservice.client;

import com.sesac.orderservice.client.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 통신 보낼
@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

}
