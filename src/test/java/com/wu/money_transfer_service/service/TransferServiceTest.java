package com.wu.money_transfer_service.service;

import com.wu.money_transfer_service.entity.Transfer;
import com.wu.money_transfer_service.model.ReceiptResponse;
import com.wu.money_transfer_service.model.TransferRequest;
import com.wu.money_transfer_service.model.TransferResponse;
import com.wu.money_transfer_service.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private TransferService transferService;

    @Captor
    private ArgumentCaptor<Transfer> transferCaptor;

    private TransferRequest transferRequest;
    private Transfer savedTransfer;
    private ReceiptResponse mockReceipt;

    @BeforeEach
    void setUp() {
        // Setup common test data
        transferRequest = new TransferRequest();
        transferRequest.setSenderAccountId("SA123456");
        transferRequest.setReceiverAccountId("RA789012");
        transferRequest.setAmount(100.00);
        transferRequest.setSenderCurrency("USD");
        transferRequest.setReceiverCurrency("EUR");

        savedTransfer = Transfer.builder()
                .transferId("TRX12345678")
                .senderAccountId("SA123456")
                .receiverAccountId("RA789012")
                .senderAmount(100.00)
                .senderCurrency("USD")
                .receiverAmount(85.00)
                .receiverCurrency("EUR")
                .exchangeRate(0.85)
                .status("SUCCESS")
                .timestamp(LocalDateTime.from(ZonedDateTime.now()))
                .build();

        mockReceipt = new ReceiptResponse();
        mockReceipt.setReceiptId("RCP12345678");
        mockReceipt.setTransferId("TRX12345678");
    }

    @Test
    void testProcessTransfer_Success() {
        // Given
        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(0.85);
        when(exchangeRateService.calculateReceiverAmount(100.00, "USD", "EUR")).thenReturn(85.00);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response = transferService.processTransfer(transferRequest);

        // Then
        verify(transferRepository).save(transferCaptor.capture());
        Transfer capturedTransfer = transferCaptor.getValue();

        // Verify transfer object properties
        assertNotNull(capturedTransfer.getTransferId());
        assertTrue(capturedTransfer.getTransferId().startsWith("TRX"));
        assertEquals("SA123456", capturedTransfer.getSenderAccountId());
        assertEquals("RA789012", capturedTransfer.getReceiverAccountId());
        assertEquals(100.00, capturedTransfer.getSenderAmount());
        assertEquals("USD", capturedTransfer.getSenderCurrency());
        assertEquals(85.00, capturedTransfer.getReceiverAmount());
        assertEquals("EUR", capturedTransfer.getReceiverCurrency());
        assertEquals(0.85, capturedTransfer.getExchangeRate());
        assertEquals("SUCCESS", capturedTransfer.getStatus());
        assertNotNull(capturedTransfer.getTimestamp());

        // Verify response properties
        assertNotNull(response);
        assertNotNull(response.getTransferId());
        assertTrue(response.getTransferId().startsWith("TRX"));
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Transfer completed successfully.", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testProcessTransfer_WithDifferentCurrencies() {
        // Given
        transferRequest.setSenderCurrency("GBP");
        transferRequest.setReceiverCurrency("JPY");

        when(exchangeRateService.getExchangeRate("GBP", "JPY")).thenReturn(150.0);
        when(exchangeRateService.calculateReceiverAmount(100.00, "GBP", "JPY")).thenReturn(15000.00);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response = transferService.processTransfer(transferRequest);

        // Then
        verify(transferRepository).save(transferCaptor.capture());
        Transfer capturedTransfer = transferCaptor.getValue();

        assertEquals("GBP", capturedTransfer.getSenderCurrency());
        assertEquals("JPY", capturedTransfer.getReceiverCurrency());
        assertEquals(150.0, capturedTransfer.getExchangeRate());
        assertEquals(15000.00, capturedTransfer.getReceiverAmount());

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testProcessTransfer_SameCurrency() {
        // Given
        transferRequest.setSenderCurrency("USD");
        transferRequest.setReceiverCurrency("USD");

        when(exchangeRateService.getExchangeRate("USD", "USD")).thenReturn(1.0);
        when(exchangeRateService.calculateReceiverAmount(100.00, "USD", "USD")).thenReturn(100.00);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response = transferService.processTransfer(transferRequest);

        // Then
        verify(transferRepository).save(transferCaptor.capture());
        Transfer capturedTransfer = transferCaptor.getValue();

        assertEquals(1.0, capturedTransfer.getExchangeRate());
        assertEquals(100.00, capturedTransfer.getReceiverAmount());

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testGetTransferReceipt_Success() {
        // Given
        when(transferRepository.findByTransferId("TRX12345678")).thenReturn(Optional.of(savedTransfer));
        when(receiptService.generateReceipt(savedTransfer)).thenReturn(mockReceipt);

        // When
        ReceiptResponse receipt = transferService.getTransferReceipt("TRX12345678");

        // Then
        verify(transferRepository).findByTransferId("TRX12345678");
        verify(receiptService).generateReceipt(savedTransfer);

        assertNotNull(receipt);
        assertEquals("RCP12345678", receipt.getReceiptId());
        assertEquals("TRX12345678", receipt.getTransferId());
    }

    @Test
    void testGetTransferReceipt_TransferNotFound() {
        // Given
        when(transferRepository.findByTransferId("NONEXISTENT")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transferService.getTransferReceipt("NONEXISTENT");
        });

        assertEquals("Transfer not found with ID: NONEXISTENT", exception.getMessage());
        verify(transferRepository).findByTransferId("NONEXISTENT");
        verify(receiptService, never()).generateReceipt(any());
    }

    @Test
    void testProcessTransfer_RepositoryException() {
        // Given
        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(0.85);
        when(exchangeRateService.calculateReceiverAmount(100.00, "USD", "EUR")).thenReturn(85.00);
        when(transferRepository.save(any(Transfer.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            transferService.processTransfer(transferRequest);
        });

        verify(exchangeRateService).getExchangeRate("USD", "EUR");
        verify(exchangeRateService).calculateReceiverAmount(100.00, "USD", "EUR");
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void testProcessTransfer_ExchangeRateServiceException() {
        // Given
        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenThrow(new RuntimeException("Exchange rate service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            transferService.processTransfer(transferRequest);
        });

        verify(exchangeRateService).getExchangeRate("USD", "EUR");
        verify(exchangeRateService, never()).calculateReceiverAmount(anyDouble(), anyString(), anyString());
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void testProcessTransfer_ZeroAmount() {
        // Given
        transferRequest.setAmount(0.0);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(0.85);
        when(exchangeRateService.calculateReceiverAmount(0.0, "USD", "EUR")).thenReturn(0.0);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response = transferService.processTransfer(transferRequest);

        // Then
        verify(transferRepository).save(transferCaptor.capture());
        Transfer capturedTransfer = transferCaptor.getValue();

        assertEquals(0.0, capturedTransfer.getSenderAmount());
        assertEquals(0.0, capturedTransfer.getReceiverAmount());

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testProcessTransfer_NegativeAmount() {
        // Given
        transferRequest.setAmount(-100.0);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(0.85);
        when(exchangeRateService.calculateReceiverAmount(-100.0, "USD", "EUR")).thenReturn(-85.0);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response = transferService.processTransfer(transferRequest);

        // Then
        verify(transferRepository).save(transferCaptor.capture());
        Transfer capturedTransfer = transferCaptor.getValue();

        assertEquals(-100.0, capturedTransfer.getSenderAmount());
        assertEquals(-85.0, capturedTransfer.getReceiverAmount());

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testProcessTransfer_TransferIdGeneration() {
        // Given
        when(exchangeRateService.getExchangeRate(anyString(), anyString())).thenReturn(0.85);
        when(exchangeRateService.calculateReceiverAmount(anyDouble(), anyString(), anyString())).thenReturn(85.00);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response1 = transferService.processTransfer(transferRequest);
        TransferResponse response2 = transferService.processTransfer(transferRequest);

        // Then
        assertNotNull(response1.getTransferId());
        assertNotNull(response2.getTransferId());
        assertNotEquals(response1.getTransferId(), response2.getTransferId());
    }
}
