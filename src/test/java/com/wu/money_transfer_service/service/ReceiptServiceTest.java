package com.wu.money_transfer_service.service;

import com.wu.money_transfer_service.entity.Transfer;
import com.wu.money_transfer_service.model.ReceiptResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @InjectMocks
    private ReceiptService receiptService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private Transfer transfer;
    private ZonedDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = ZonedDateTime.of(2023, 5, 15, 10, 30, 0, 0, ZoneId.of("UTC"));

        transfer = new Transfer();
        transfer.setTransferId("TRF12345678");
        transfer.setSenderAccountId("SA98765432");
        transfer.setReceiverAccountId("RA12345678");
        transfer.setSenderAmount(100.00);
        transfer.setSenderCurrency("USD");
        transfer.setReceiverAmount(85.00);
        transfer.setReceiverCurrency("EUR");
        transfer.setExchangeRate(0.85);
        transfer.setStatus("COMPLETED");
        transfer.setTimestamp(LocalDateTime.from(timestamp));
    }

    @Test
    void testGenerateReceiptWithValidTransfer() {
        // When generating a receipt for a valid transfer
        ReceiptResponse receipt = receiptService.generateReceipt(transfer);

        // Then all fields should be correctly mapped
        assertEquals("RCP12345678", receipt.getReceiptId());
        assertEquals("TRF12345678", receipt.getTransferId());
        assertEquals("SA98765432", receipt.getSenderAccountId());
        assertEquals("RA12345678", receipt.getReceiverAccountId());
        assertEquals(100.00, receipt.getSenderAmount());
        assertEquals("USD", receipt.getSenderCurrency());
        assertEquals(85.00, receipt.getReceiverAmount());
        assertEquals("EUR", receipt.getReceiverCurrency());
        assertEquals(0.85, receipt.getExchangeRate());
        assertEquals("COMPLETED", receipt.getStatus());
        assertEquals(timestamp.format(DATE_FORMATTER), receipt.getTimestamp());
    }

    @Test
    void testGenerateReceiptWithNullTimestamp() {
        // Given a transfer with null timestamp
        transfer.setTimestamp(null);

        // When generating a receipt
        ReceiptResponse receipt = receiptService.generateReceipt(transfer);

        // Then the timestamp field should be empty
        assertEquals("", receipt.getTimestamp());

        // And other fields should still be correctly mapped
        assertEquals("RCP12345678", receipt.getReceiptId());
        assertEquals("TRF12345678", receipt.getTransferId());
    }

    @Test
    void testGenerateReceiptWithDifferentTransferId() {
        // Given a transfer with a different ID format
        transfer.setTransferId("TRF-ABCDEFG");

        // When generating a receipt
        ReceiptResponse receipt = receiptService.generateReceipt(transfer);

        // Then the receipt ID should be derived correctly
        assertEquals("RCP-ABCDEFG", receipt.getReceiptId());
    }

    @Test
    void testGenerateReceiptWithMinimalTransferData() {
        // Given a transfer with minimal data
        Transfer minimalTransfer = new Transfer();
        minimalTransfer.setTransferId("TRF99999999");

        // When generating a receipt
        ReceiptResponse receipt = receiptService.generateReceipt(minimalTransfer);

        // Then the receipt should have the correct ID and null/default values for other fields
        assertEquals("RCP99999999", receipt.getReceiptId());
        assertEquals("TRF99999999", receipt.getTransferId());
        assertNull(receipt.getSenderAccountId());
        assertNull(receipt.getReceiverAccountId());
        assertEquals(0.0, receipt.getSenderAmount());
        assertNull(receipt.getSenderCurrency());
        assertEquals(0.0, receipt.getReceiverAmount());
        assertNull(receipt.getReceiverCurrency());
        assertEquals(0.0, receipt.getExchangeRate());
        assertNull(receipt.getStatus());
        assertEquals("", receipt.getTimestamp());
    }

    @Test
    void testGenerateReceiptWithDecimalPrecision() {
        // Given a transfer with precise decimal values
        transfer.setSenderAmount(123.45);
        transfer.setReceiverAmount(104.93);
        transfer.setExchangeRate(0.8500);

        // When generating a receipt
        ReceiptResponse receipt = receiptService.generateReceipt(transfer);

        // Then the decimal values should be preserved
        assertEquals(123.45, receipt.getSenderAmount());
        assertEquals(104.93, receipt.getReceiverAmount());
        assertEquals(0.8500, receipt.getExchangeRate());
    }

    @Test
    void testGenerateReceiptWithDifferentStatus() {
        // Given transfers with different statuses
        String[] statuses = {"PENDING", "PROCESSING", "FAILED", "CANCELLED"};

        for (String status : statuses) {
            transfer.setStatus(status);

            // When generating receipts
            ReceiptResponse receipt = receiptService.generateReceipt(transfer);

            // Then the status should be correctly mapped
            assertEquals(status, receipt.getStatus());
        }
    }

    @Test
    void testGenerateReceiptWithDifferentTimezones() {
        // Given transfers with timestamps in different timezones
        ZonedDateTime estTime = ZonedDateTime.of(2023, 5, 15, 10, 30, 0, 0, ZoneId.of("America/New_York"));
        ZonedDateTime jstTime = ZonedDateTime.of(2023, 5, 15, 10, 30, 0, 0, ZoneId.of("Asia/Tokyo"));

        // Test EST timezone
        transfer.setTimestamp(LocalDateTime.from(estTime));
        ReceiptResponse estReceipt = receiptService.generateReceipt(transfer);
        assertEquals(estTime.format(DATE_FORMATTER), estReceipt.getTimestamp());

        // Test JST timezone
        transfer.setTimestamp(LocalDateTime.from(jstTime));
        ReceiptResponse jstReceipt = receiptService.generateReceipt(transfer);
        assertEquals(jstTime.format(DATE_FORMATTER), jstReceipt.getTimestamp());
    }
}
