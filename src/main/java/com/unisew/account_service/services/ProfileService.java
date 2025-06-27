package com.unisew.account_service.services;

import com.unisew.account_service.requests.CreateProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "profile-service", url = "http://localhost:8085/api/v2/profile")
public interface ProfileService {

    @PostMapping("")
    Map<String, Object> createProfile(@RequestBody CreateProfileRequest request);

    @GetMapping("")
    Map<String, Object> getProfile(@RequestParam(name = "accountId") int accountId);
}
