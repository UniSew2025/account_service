package com.unisew.account_service.services;

import com.unisew.account_service.requests.CreateProfileRequest;
import com.unisew.account_service.responses.ResponseObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "profile-service", url = "http://localhost:8085/api/v2/profile")
public interface ProfileService {

    @PostMapping("")
    ResponseEntity<ResponseObject> createProfile(@RequestBody CreateProfileRequest request);

    @GetMapping("")
    ResponseEntity<ResponseObject> getProfile(@RequestParam(name = "accountId") int accountId);
}
