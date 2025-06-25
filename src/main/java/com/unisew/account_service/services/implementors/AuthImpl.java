package com.unisew.account_service.services.implementors;

import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.requests.CreateProfileRequest;
import com.unisew.account_service.requests.LoginRequest;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.services.AuthService;
import com.unisew.account_service.services.JWTService;
import com.unisew.account_service.services.ProfileService;
import com.unisew.account_service.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthImpl implements AuthService {

    private final AccountRepo accountRepo;

    private final ProfileService profileService;

    private final JWTService jwtService;

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
            return createAccount(request);
        }
        Map<String, Object> data = buildAccountResponse(account);
        data.put("profile", profileService.getProfile(account.getId()));

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", data);
    }

    private ResponseEntity<ResponseObject> createAccount(LoginRequest request) {
        Account account = accountRepo.save(
                Account.builder()
                        .registerDate(LocalDate.now())
                        .email(request.getEmail())
                        .role(Role.SCHOOL)
                        .status(Status.ACCOUNT_ACTIVE)
                        .build()
        );

        CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
                .accountId(account.getId())
                .avatar(request.getAvatar())
                .name(request.getName())
                .role(account.getRole().getValue().toLowerCase())
                .build();

        Map<String, Object> data = buildAccountResponse(account);
        data.put("profile", profileService.createProfile(createProfileRequest));

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", data);
    }

    private Map<String, Object> buildAccountResponse(Account account) {
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("id", account.getId());
        accountData.put("email", account.getEmail());
        accountData.put("role", account.getRole().getValue().toLowerCase());
        accountData.put("status", account.getStatus().getValue().toLowerCase());
        accountData.put("registerDate", account.getRegisterDate());
        accountData.put("token", jwtService.generateToken(account.getEmail(), account.getRole().getValue(), account.getId()));
        return accountData;
    }
}
