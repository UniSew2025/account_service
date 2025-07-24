package com.unisew.account_service.responses;
import com.unisew.account_service.enums.RegisterType;
import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
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
public class AccountResponseDTO {
    private Integer id;
    private String email;
    private Role role;
    private LocalDate registerDate;
    private String status;
    private Object partner;
}
