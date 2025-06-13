package com.unisew.account_service;

import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.repositories.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
@RequiredArgsConstructor
public class AccountServiceApplication {

    private final AccountRepo accountRepo;

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if(accountRepo.count() == 0){
                    accountRepo.save(
                            Account.builder()
                                    .email("unisewsu2025@gmail.com")
                                    .role(Role.ADMIN)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );
                }
            }
        };
    }
}
