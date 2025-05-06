package com.wu.money_transfer_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        exchangeRateService = new ExchangeRateService(restTemplate);
    }

    @Test
    void testGetExchangeRateForSameCurrency() {
        // When the source and target currencies are the same
        double rate = exchangeRateService.getExchangeRate("USD", "USD");

        // Then the exchange rate should be 1.0
        assertEquals(1.0, rate, "Exchange rate for same currency should be 1.0");
    }

    @Test
    void testGetExchangeRateFromUsdToEur() {
        // When converting from USD to EUR
        double rate = exchangeRateService.getExchangeRate("USD", "EUR");

        // Then the exchange rate should match the predefined rate
        assertEquals(0.85, rate, "USD to EUR rate should match predefined value");
    }

    @Test
    void testGetExchangeRateFromEurToUsd() {
        // When converting from EUR to USD
        double rate = exchangeRateService.getExchangeRate("EUR", "USD");

        // Then the exchange rate should be the inverse of EUR rate
        assertEquals(1.0 / 0.85, rate, 0.0001, "EUR to USD rate should be inverse of USD to EUR");
    }

    @Test
    void testGetExchangeRateFromEurToGbp() {
        // When converting between two non-USD currencies
        double rate = exchangeRateService.getExchangeRate("EUR", "GBP");

        // Then the rate should be calculated via USD
        double expectedRate = (1.0 / 0.85) * 0.74;
        assertEquals(expectedRate, rate, 0.0001, "EUR to GBP should convert via USD");
    }

    @Test
    void testGetExchangeRateWithUnknownSourceCurrency() {
        // When source currency is unknown
        double rate = exchangeRateService.getExchangeRate("XYZ", "USD");

        // Then default rate of 1.0 should be used
        assertEquals(1.0, rate, "Unknown source currency should use default rate");
    }

    @Test
    void testGetExchangeRateWithUnknownTargetCurrency() {
        // When target currency is unknown
        double rate = exchangeRateService.getExchangeRate("USD", "XYZ");

        // Then default rate of 1.0 should be used
        assertEquals(1.0, rate, "Unknown target currency should use default rate");
    }

    @ParameterizedTest
    @CsvSource({
            "USD, EUR, 100, 85.0",
            "EUR, USD, 100, 117.65",
            "USD, USD, 100, 100.0",
            "GBP, JPY, 100, 148.65",
            "XYZ, USD, 100, 100.0",
            "USD, XYZ, 100, 100.0"
    })
    void testCalculateReceiverAmount(String fromCurrency, String toCurrency,
                                     double amount, double expected) {
        // When calculating receiver amount
        double receiverAmount = exchangeRateService.calculateReceiverAmount(
                amount, fromCurrency, toCurrency);

        // Then the result should match expected value
        assertEquals(expected, receiverAmount, 0.01,
                String.format("Converting %s %s to %s should yield %s",
                        amount, fromCurrency, toCurrency, expected));
    }

    @Test
    void testAllPredefinedCurrencies() {
        // Test all predefined currency pairs to ensure they're initialized correctly
        String[] currencies = {"EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "MXN", "BRL"};

        for (String currency : currencies) {
            // USD to currency
            double usdToCurrency = exchangeRateService.getExchangeRate("USD", currency);
            assertNotEquals(1.0, usdToCurrency, "USD to " + currency + " rate should be defined");

            // Currency to USD
            double currencyToUsd = exchangeRateService.getExchangeRate(currency, "USD");
            assertEquals(1.0 / usdToCurrency, currencyToUsd, 0.0001,
                    currency + " to USD rate should be inverse of USD to " + currency);
        }
    }

    @Test
    void testRoundingInCalculateReceiverAmount() {
        // When calculating an amount that would have more than 2 decimal places
        double receiverAmount = exchangeRateService.calculateReceiverAmount(
                33.33, "USD", "EUR");

        // Then the result should be rounded to 2 decimal places
        assertEquals(28.33, receiverAmount, 0.001, "Result should be rounded to 2 decimal places");
    }

    @Test
    void testInitializationWithMockRates() {
        // Verify the service is initialized with mock rates
        // This is mostly testing that no exceptions are thrown during initialization
        ExchangeRateService service = new ExchangeRateService(restTemplate);
        assertNotNull(service, "Service should be initialized successfully");

        // Test a sample rate to ensure initialization worked
        double rate = service.getExchangeRate("USD", "EUR");
        assertEquals(0.85, rate, "Service should be initialized with mock rates");
    }
}
