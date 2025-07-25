package com.unisew.account_service;

import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.repositories.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
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
                                    .email("nguyenngoctram762@gmail.com")
                                    .role(Role.ADMIN)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    accountRepo.save(
                            Account.builder()
                                    .email("unisewsu2025@gmail.com")
                                    .role(Role.ADMIN)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    accountRepo.save(
                            Account.builder()
                                    .email("nguyenngoctram76213@gmail.com")
                                    .role(Role.DESIGNER)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    accountRepo.save(
                            Account.builder()
                                    .email("nguyenngoctram7621@gmail.com")
                                    .role(Role.GARMENT_FACTORY)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    accountRepo.save(
                            Account.builder()
                                    .email("abc7621@gmail.com")
                                    .role(Role.GARMENT_FACTORY)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );
                }
            }
        };
    }
}
