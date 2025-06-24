package com.unisew.account_service.requests;
import com.unisew.account_service.enums.RegisterType;
import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AccountRequestDTO {
    @NotBlank(message = "email is required")
    @Email
    private String email;
    private Role role;
    private String name;
    private RegisterType registerType;
    private String status;
}
