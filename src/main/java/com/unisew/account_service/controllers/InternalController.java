package com.unisew.account_service.controllers;

import com.unisew.account_service.services.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/account")
public class InternalController {

    private final AccountService accountService;

    @Hidden
    @GetMapping("")
    public Map<String, Object> getAccountById(@RequestParam(name = "id") int id) {
        return accountService.getAccountById(id);
    }
}
