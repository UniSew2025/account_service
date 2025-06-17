package com.unisew.account_service.requests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private String newStatus; // As String to map from request, convert to Enum in service
    private String paymentGatewayCode;
    private String paymentGatewayMessage;
}
