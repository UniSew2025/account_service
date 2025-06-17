package com.unisew.account_service.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletRequestDTO {
    private long amount;
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private String note;
}
