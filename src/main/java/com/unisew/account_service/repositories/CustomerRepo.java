package com.unisew.account_service.repositories;

import com.unisew.account_service.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
}
