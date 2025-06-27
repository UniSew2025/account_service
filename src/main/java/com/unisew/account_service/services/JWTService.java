package com.unisew.account_service.services;

public interface JWTService {
    String generateToken(String email, String role, int id);
}
