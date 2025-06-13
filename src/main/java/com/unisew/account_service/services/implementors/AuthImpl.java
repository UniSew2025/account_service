package com.unisew.account_service.services.implementors;

import com.unisew.account_service.models.Account;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.requests.LoginRequest;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.services.AuthService;
import com.unisew.account_service.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthImpl implements AuthService {

    private final AccountRepo accountRepo;

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    @Value("${google.response_type}")
    private String responseType;

    @Value("${google.scope}")
    private String scope;

    @Override
    public ResponseEntity<ResponseObject> getGoogleUrl() {
        Map<String, Object> data = new HashMap<>();
        String url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=" + responseType
                + "&scope=" + scope;
        data.put("url", url);

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request) {
        Account account = accountRepo.findByEmail(request.getEmail()).orElse(null);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid email", null);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", account.getId());
        data.put("email", account.getEmail());
        data.put("role", account.getRole().getValue().toLowerCase());
        data.put("status", account.getStatus().getValue().toLowerCase());
        data.put("registerDate", account.getRegisterDate());

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", data);
    }
}
