package com.unisew.account_service.repositories;

import com.unisew.account_service.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet, Integer> {
}
