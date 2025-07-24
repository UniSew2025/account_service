package com.unisew.account_service.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "http://localhost:8084/api/v2/order")
public interface OrderService {

    @GetMapping("")
    boolean isSafeToBan(@RequestParam(name = "garmentId") int garmentId);
}
