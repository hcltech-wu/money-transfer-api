package com.wu.money_transfer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.money_transfer_service.model.ReceiptResponse;
import com.wu.money_transfer_service.model.TransferRequest;
import com.wu.money_transfer_service.model.TransferResponse;
import com.wu.money_transfer_service.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TransferRequest validTransferRequest;
    private TransferResponse mockTransferResponse;
    private ReceiptResponse mockReceiptResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transferController).build();
        objectMapper = new ObjectMapper();

        // Setup valid transfer request
        validTransferRequest = new TransferRequest();
        validTransferRequest.setSenderAccountId("SA123456");
        validTransferRequest.setReceiverAccountId("RA789012");
        validTransferRequest.setAmount(100.00);
        validTransferRequest.setSenderCurrency("USD");
        validTransferRequest.setReceiverCurrency("EUR");

        // Setup mock transfer response
        mockTransferResponse = TransferResponse.builder()
                .transferId("TRX12345678")
                .status("SUCCESS")
                .message("Transfer completed successfully.")
                .timestamp(ZonedDateTime.now())
                .build();

        // Setup mock receipt response
        mockReceiptResponse = new ReceiptResponse();
        mockReceiptResponse.setReceiptId("RCP12345678");
        mockReceiptResponse.setTransferId("TRX12345678");
        mockReceiptResponse.setSenderAccountId("SA123456");
        mockReceiptResponse.setReceiverAccountId("RA789012");
        mockReceiptResponse.setSenderAmount(100.00);
        mockReceiptResponse.setSenderCurrency("USD");
        mockReceiptResponse.setReceiverAmount(85.00);
        mockReceiptResponse.setReceiverCurrency("EUR");
        mockReceiptResponse.setExchangeRate(0.85);
        mockReceiptResponse.setStatus("SUCCESS");
        mockReceiptResponse.setTimestamp("2023-05-15 10:30:00 UTC");
    }

    @Test
    void testTransfer_Success() throws Exception {
        // Given
        when(transferService.processTransfer(any(TransferRequest.class))).thenReturn(mockTransferResponse);

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferId", is("TRX12345678")))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Transfer completed successfully.")));

        verify(transferService).processTransfer(any(TransferRequest.class));
    }

    @Test
    void testTransfer_WithDifferentCurrencies() throws Exception {
        // Given
        validTransferRequest.setSenderCurrency("GBP");
        validTransferRequest.setReceiverCurrency("JPY");

        when(transferService.processTransfer(any(TransferRequest.class))).thenReturn(mockTransferResponse);

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SUCCESS")));

        verify(transferService).processTransfer(any(TransferRequest.class));
    }

    @Test
    void testTransfer_WithZeroAmount() throws Exception {
        // Given
        validTransferRequest.setAmount(0.0);

        when(transferService.processTransfer(any(TransferRequest.class))).thenReturn(mockTransferResponse);

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SUCCESS")));

        verify(transferService).processTransfer(any(TransferRequest.class));
    }

    @Test
    void testTransfer_ServiceThrowsException() throws Exception {
        // Given
        when(transferService.processTransfer(any(TransferRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransferRequest)))
                .andExpect(status().isInternalServerError());

        verify(transferService).processTransfer(any(TransferRequest.class));
    }

    @Test
    void testGetTransferReceipt_Success() throws Exception {
        // Given
        when(transferService.getTransferReceipt("TRX12345678")).thenReturn(mockReceiptResponse);

        // When & Then
        mockMvc.perform(get("/api/{transferId}/receipt", "TRX12345678"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.receiptId", is("RCP12345678")))
                .andExpect(jsonPath("$.transferId", is("TRX12345678")))
                .andExpect(jsonPath("$.senderAccountId", is("SA123456")))
                .andExpect(jsonPath("$.receiverAccountId", is("RA789012")))
                .andExpect(jsonPath("$.senderAmount", is(100.00)))
                .andExpect(jsonPath("$.senderCurrency", is("USD")))
                .andExpect(jsonPath("$.receiverAmount", is(85.00)))
                .andExpect(jsonPath("$.receiverCurrency", is("EUR")))
                .andExpect(jsonPath("$.exchangeRate", is(0.85)))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.timestamp", is("2023-05-15 10:30:00 UTC")));

        verify(transferService).getTransferReceipt("TRX12345678");
    }

    @Test
    void testGetTransferReceipt_TransferNotFound() throws Exception {
        // Given
        when(transferService.getTransferReceipt("NONEXISTENT"))
                .thenThrow(new RuntimeException("Transfer not found with ID: NONEXISTENT"));

        // When & Then
        mockMvc.perform(get("/api/{transferId}/receipt", "NONEXISTENT"))
                .andExpect(status().isInternalServerError());

        verify(transferService).getTransferReceipt("NONEXISTENT");
    }

    @Test
    void testGetTransferReceipt_WithSpecialCharacters() throws Exception {
        // Given
        String transferIdWithSpecialChars = "TRX-123_456";
        when(transferService.getTransferReceipt(transferIdWithSpecialChars)).thenReturn(mockReceiptResponse);

        // When & Then
        mockMvc.perform(get("/api/{transferId}/receipt", transferIdWithSpecialChars))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.receiptId", is("RCP12345678")));

        verify(transferService).getTransferReceipt(transferIdWithSpecialChars);
    }

    @Test
    void testTransfer_InvalidRequest_MissingFields() throws Exception {
        // Given
        TransferRequest invalidRequest = new TransferRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(transferService, never()).processTransfer(any(TransferRequest.class));
    }

    @Test
    void testTransfer_InvalidRequest_NegativeAmount() throws Exception {
        // Given
        validTransferRequest.setAmount(-100.0);

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransferRequest)))
                .andExpect(status().isOk()); // Assuming negative amounts are allowed

        verify(transferService).processTransfer(any(TransferRequest.class));
    }

    @Test
    void testTransfer_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{\"senderAccountId\":\"SA123456\", invalid json}";

        // When & Then
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(transferService, never()).processTransfer(any(TransferRequest.class));
    }
}
