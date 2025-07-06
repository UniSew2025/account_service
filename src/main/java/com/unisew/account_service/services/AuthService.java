package com.unisew.account_service.services;

import com.unisew.account_service.requests.LoginRequest;
import com.unisew.account_service.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> getGoogleUrl();

    ResponseEntity<ResponseObject> login(LoginRequest request);

    ResponseEntity<ResponseObject> getGoogleAccessToken(int accountId);
}
