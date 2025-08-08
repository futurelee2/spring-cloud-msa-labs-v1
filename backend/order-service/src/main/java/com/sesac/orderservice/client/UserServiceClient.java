package com.sesac.orderservice.client;

import com.sesac.orderservice.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service") //  유레카가 알아서 통신 가능
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
