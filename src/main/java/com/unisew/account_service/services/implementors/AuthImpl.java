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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

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

    @Value("${google.client_secret}")
    private String clientSecret;

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
                + "&scope=" + scope
                + "&access_type=offline"
                + "&prompt=consent";
        data.put("url", url);

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request) {
        Account account = accountRepo.findByEmail(request.getEmail()).orElse(null);
        if (account == null) {
            return createAccount(request);
        }
        if(account.getStatus().equals(Status.ACCOUNT_INACTIVE)){
            return ResponseBuilder.build(HttpStatus.UNAUTHORIZED, "Account is inactive", account);
        }
        account.setGgRefreshToken(request.getRefreshToken());
        account = accountRepo.save(account);
        Map<String, Object> data = buildAccountResponse(account);
        data.put("profile", profileService.getProfile(account.getId()));

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", data);
    }

    @Override
    public ResponseEntity<ResponseObject> getGoogleAccessToken(int accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null || account.getGgRefreshToken() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No refresh token for this account")
                            .build()
            );
        }
        String refreshToken = account.getGgRefreshToken();
        try {
            String accessToken = getGoogleAccessTokenFromRefreshToken(refreshToken);
            Map<String, Object> res = new HashMap<>();
            res.put("access_token", accessToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(res)
                            .message("Google access token fetched successfully")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Cannot get access token: " + e.getMessage())
                            .build()
            );

        }
    }

    private String getGoogleAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> resp = restTemplate.postForObject(tokenEndpoint, request, Map.class);
        if (resp != null && resp.containsKey("access_token")) {
            return (String) resp.get("access_token");
        }
        throw new Exception("No access_token in response");
    }

    private ResponseEntity<ResponseObject> createAccount(LoginRequest request) {
        Account account = accountRepo.save(
                Account.builder()
                        .registerDate(LocalDate.now())
                        .email(request.getEmail())
                        .role(Role.SCHOOL)
                        .ggRefreshToken(request.getRefreshToken())
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
