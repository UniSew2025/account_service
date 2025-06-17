package com.unisew.account_service.responses;
import com.unisew.account_service.enums.PaymentType;
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
public class TransactionResponseDTO {
    private Integer id;
    private String senderName;
    private String receiverName;
    private long amount;
    private PaymentType paymentType;
    private String note;
    private LocalDate creationDate;
    private Status status;
    private String paymentGatewayCode;
    private String paymentGatewayMessage;
    private Integer orderId;
    private Integer designRequestId;
    private Integer walletId;
}
