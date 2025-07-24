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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AccountRequestDTO {
    private String email;
    private Role role;
    private String status;
    private List<Integer> packageIds;
    private int garmentId;
}
