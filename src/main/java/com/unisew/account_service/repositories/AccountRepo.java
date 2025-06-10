package com.unisew.account_service.repositories;

import com.unisew.account_service.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, Integer> {
}
