package com.wu.money_transfer_service.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "transfers")
public class Transfer {


    private String transferId;
    private String senderAccountId;
    private String receiverAccountId;
    private Double senderAmount;
    private String senderCurrency;
    private Double receiverAmount;
    private String receiverCurrency;
    private Double exchangeRate;
    private String status;
    private LocalDateTime timestamp;
    private String receiptUrl;

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getTransferId() {
        return transferId;
    }
    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }
    public static Builder builder() {
        return new Builder();
    }

   /* public Transfer orElseThrow(Object o) {
        return null;
    }*/

    // Builder class
    public static class Builder {
        private Transfer transfer = new Transfer();

        public Builder transferId(String transferId) {
            transfer.transferId = transferId;
            return this;
        }

        public Builder senderAccountId(String senderAccountId) {
            transfer.senderAccountId = senderAccountId;
            return this;
        }

        public Builder receiverAccountId(String receiverAccountId) {
            transfer.receiverAccountId = receiverAccountId;
            return this;
        }

        public Builder senderAmount(Double senderAmount) {
            transfer.senderAmount = senderAmount;
            return this;
        }

        public Builder senderCurrency(String senderCurrency) {
            transfer.senderCurrency = senderCurrency;
            return this;
        }

        public Builder receiverAmount(Double receiverAmount) {
            transfer.receiverAmount = receiverAmount;
            return this;
        }

        public Builder receiverCurrency(String receiverCurrency) {
            transfer.receiverCurrency = receiverCurrency;
            return this;
        }

        public Builder exchangeRate(Double exchangeRate) {
            transfer.exchangeRate = exchangeRate;
            return this;
        }

        public Builder status(String status) {
            transfer.status = status;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            transfer.timestamp = timestamp;
            return this;
        }

        public Builder receiptUrl(String receiptUrl) {
            transfer.receiptUrl = receiptUrl;
            return this;
        }

        public Transfer build() {
            return transfer;
        }
    }
}
