package com.wu.money_transfer_service.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRateService {
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    private final RestTemplate restTemplate;

    // Common currency exchange rates against USD (as of a recent date)
    private final Map<String, Double> usdRates = new HashMap<>();

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        initializeDefaultRates();
        log.info("ExchangeRateService initialized with mock exchange rates");
    }

    private void initializeDefaultRates() {
        // Initialize with some default rates
        usdRates.put("EUR", 0.85);
        usdRates.put("GBP", 0.74);
        usdRates.put("JPY", 110.0);
        usdRates.put("CAD", 1.25);
        usdRates.put("AUD", 1.35);
        usdRates.put("CHF", 0.92);
        usdRates.put("CNY", 6.45);
        usdRates.put("INR", 74.5);
        usdRates.put("MXN", 20.0);
        usdRates.put("BRL", 5.25);
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        log.info("Getting mock exchange rate from {} to {}", fromCurrency, toCurrency);

        // If currencies are the same, return 1.0
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }

        // Convert from source currency to USD first (if not already USD)
        double fromToUsd = 1.0;
        if (!fromCurrency.equals("USD")) {
            Double rate = usdRates.get(fromCurrency);
            if (rate != null) {
                fromToUsd = 1.0 / rate;  // Inverse rate to convert to USD
            } else {
                log.warn("No rate found for {}, using default", fromCurrency);
                fromToUsd = 1.0;
            }
        }

        // Then convert from USD to target currency (if not USD)
        double usdToTarget = 1.0;
        if (!toCurrency.equals("USD")) {
            Double rate = usdRates.get(toCurrency);
            if (rate != null) {
                usdToTarget = rate;
            } else {
                log.warn("No rate found for {}, using default", toCurrency);
                usdToTarget = 1.0;
            }
        }

        // Calculate the final exchange rate
        double exchangeRate = fromToUsd * usdToTarget;
        log.info("Mock exchange rate from {} to {}: {}", fromCurrency, toCurrency, exchangeRate);

        return exchangeRate;
    }

    public double calculateReceiverAmount(double senderAmount, String senderCurrency, String receiverCurrency) {
        double exchangeRate = getExchangeRate(senderCurrency, receiverCurrency);
        double receiverAmount = senderAmount * exchangeRate;

        // Round to 2 decimal places
        receiverAmount = Math.round(receiverAmount * 100.0) / 100.0;

        log.info("Calculated receiver amount: {} {} = {} {}",
                senderAmount, senderCurrency, receiverAmount, receiverCurrency);

        return receiverAmount;
    }
}
