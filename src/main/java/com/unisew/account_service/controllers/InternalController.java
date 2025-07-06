package com.unisew.account_service.controllers;

import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.services.AccountService;
import com.unisew.account_service.services.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/account")
public class InternalController {

    private final AccountService accountService;
    private final AuthService authService;

    @Hidden
    @GetMapping("")
    public Map<String, Object> getAccountById(@RequestParam(name = "id") int id) {
        return accountService.getAccountById(id);
    }

    @Hidden
    @GetMapping("/google-access-token/{accountId}")
    public ResponseEntity<ResponseObject> getGoogleAccessToken(@PathVariable int accountId) {
        System.out.println("====> IN GOOGLE ACCESS TOKEN CONTROLLER: " + accountId);
        return authService.getGoogleAccessToken(accountId);
    }
}
