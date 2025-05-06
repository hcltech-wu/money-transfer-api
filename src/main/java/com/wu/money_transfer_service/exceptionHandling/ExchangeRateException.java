package com.wu.money_transfer_service.exceptionHandling;

public class ExchangeRateException extends RuntimeException {
    public ExchangeRateException(String message) {
        super(message);
    }
}
