package com.wu.money_transfer_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for money transfer")
public class TransferRequest {

    @NotBlank(message = "Sender account ID is required")
    @Schema(description = "Sender's account ID", example = "12345")
    private String senderAccountId;

    @NotBlank(message = "Receiver account ID is required")
    @Schema(description = "Receiver's account ID", example = "67890")
    private String receiverAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount to transfer", example = "150.75")
    private Double amount;

    @NotBlank(message = "Sender currency is required")
    @Schema(description = "Currency of the sender", example = "USD")
    private String senderCurrency;

    @NotBlank(message = "Receiver currency is required")
    @Schema(description = "Currency of the receiver", example = "EUR")
    private String receiverCurrency;

    // Getters and setters
    public String getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(String senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(String receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSenderCurrency() {
        return senderCurrency;
    }

    public void setSenderCurrency(String senderCurrency) {
        this.senderCurrency = senderCurrency;
    }

    public String getReceiverCurrency() {
        return receiverCurrency;
    }

    public void setReceiverCurrency(String receiverCurrency) {
        this.receiverCurrency = receiverCurrency;
    }

    // For backward compatibility with existing code that might use getCurrency()
    public String getCurrency() {
        return senderCurrency;
    }

    // For backward compatibility with existing code that might use setCurrency()
    public void setCurrency(String currency) {
        this.senderCurrency = currency;
    }
}
