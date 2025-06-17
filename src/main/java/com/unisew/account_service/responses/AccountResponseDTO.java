package com.unisew.account_service.responses;
import com.unisew.account_service.enums.RegisterType;
import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private Integer id;
    private String email;
    private String name;
    private Role role;
    private LocalDate registerDate;
    private Status status;
}
