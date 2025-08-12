package com.sesac.orderservice.facade;

import com.sesac.orderservice.client.UserServiceClient;
import com.sesac.orderservice.client.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceFacade {

    private final UserServiceClient userServiceClient; // Openfeign 추상화 클라이언트

    // fallbackMethod = 메서드의 이름을 가리킴
    @CircuitBreaker(name = "user-service", fallbackMethod = "getUserFallback") //기존 메서드를 wrapping 해서 회복성 패턴으로 감싸기
    @Retry(name = "user-service")
    public UserDto getUserWithFallback(Long userId){
        log.info("User service 호출 시도 - userId = {}", userId);
        return userServiceClient.getUserById(userId);
    }
    
    // Fallback 메서드 : User-Service 장애 시 기존 사용자 정보 반환
    public UserDto getUserFallback(Long userId, Throwable ex){
        // 연결이 안되었을 때, 대안으로 만들어줌
        // 연결이 되면, 추후에 채워넣어줌

        log.warn("User Service 장애 감지! Fallback 실행 - userId : {}, 에러 : {}", userId, ex.getMessage());
        UserDto defaultUser = new UserDto();
        defaultUser.setId(userId);
        defaultUser.setName("임시 사용자");
        defaultUser.setEmail("temp@example.com");
        return defaultUser;
    }
}
