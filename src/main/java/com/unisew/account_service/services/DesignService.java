package com.unisew.account_service.services;

import com.unisew.account_service.requests.GetAllDesignByPackageIdRequest;
import com.unisew.account_service.responses.ResponseObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "design-service", url = "http://localhost:8082/api/v2/design")
public interface DesignService {

    @PostMapping("")
    boolean isSafeToBan(@RequestBody List<Integer> packageIds);
}
