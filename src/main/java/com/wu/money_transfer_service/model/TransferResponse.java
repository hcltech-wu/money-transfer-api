package com.wu.money_transfer_service.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Response model for money transfer")
public class TransferResponse {

    @Schema(description = "Unique transfer ID", example = "TRX987654321")
    private String transferId;

    @Schema(description = "Status of the transfer", example = "SUCCESS")
    private String status;

    @Schema(description = "Message describing the result", example = "Transfer completed successfully.")
    private String message;

    @Schema(description = "Timestamp of the transfer", example = "2025-04-27T15:00:00Z")
    private ZonedDateTime timestamp;

    // Getters and setters
    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ZonedDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }

    // Static method to create a builder
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private final TransferResponse response = new TransferResponse();

        public Builder transferId(String transferId) {
            response.transferId = transferId;
            return this;
        }

        public Builder status(String status) {
            response.status = status;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public Builder timestamp(ZonedDateTime timestamp) {
            response.timestamp = timestamp;
            return this;
        }

        public TransferResponse build() {
            return response;
        }
    }
}
