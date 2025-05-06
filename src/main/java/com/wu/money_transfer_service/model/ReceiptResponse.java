package com.wu.money_transfer_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model for transfer receipt")
public class ReceiptResponse {

    @Schema(description = "Unique receipt ID", example = "RCP12345678")
    private String receiptId;

    @Schema(description = "Transfer ID associated with this receipt", example = "TRX12345678")
    private String transferId;

    @Schema(description = "Sender's account ID", example = "ACC123456")
    private String senderAccountId;

    @Schema(description = "Receiver's account ID", example = "ACC789012")
    private String receiverAccountId;

    @Schema(description = "Amount sent by the sender", example = "100.00")
    private Double senderAmount;

    @Schema(description = "Currency of the sender", example = "USD")
    private String senderCurrency;

    @Schema(description = "Amount received by the receiver", example = "85.00")
    private Double receiverAmount;

    @Schema(description = "Currency of the receiver", example = "EUR")
    private String receiverCurrency;

    @Schema(description = "Exchange rate used for the transfer", example = "0.85")
    private Double exchangeRate;

    @Schema(description = "Status of the transfer", example = "SUCCESS")
    private String status;

    @Schema(description = "Timestamp of the transfer", example = "2025-05-05 14:30:45 UTC")
    private String timestamp;

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

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

    public Double getSenderAmount() {
        return senderAmount;
    }

    public void setSenderAmount(Double senderAmount) {
        this.senderAmount = senderAmount;
    }

    public String getSenderCurrency() {
        return senderCurrency;
    }

    public void setSenderCurrency(String senderCurrency) {
        this.senderCurrency = senderCurrency;
    }

    public Double getReceiverAmount() {
        return receiverAmount;
    }

    public void setReceiverAmount(Double receiverAmount) {
        this.receiverAmount = receiverAmount;
    }

    public String getReceiverCurrency() {
        return receiverCurrency;
    }

    public void setReceiverCurrency(String receiverCurrency) {
        this.receiverCurrency = receiverCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
