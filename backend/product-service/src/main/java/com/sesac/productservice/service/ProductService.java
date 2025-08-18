package com.sesac.productservice.service;

import com.sesac.productservice.entity.Product;
import com.sesac.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("상품을 찾을 수 없습니다: " + id)
        );
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        if(product.getStockQuantity() <  quantity){
            throw new RuntimeException("재고가 부족합니다.");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        log.info("재고 차감 완료");

    }

    @Transactional
    public void restoreStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("상품을 찾을 수 없습니다: " + productId));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);


    }
}
