package com.wu.money_transfer_service.exceptionHandling;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }
}
