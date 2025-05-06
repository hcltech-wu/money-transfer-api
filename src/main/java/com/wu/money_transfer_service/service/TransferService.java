package com.wu.money_transfer_service.service;

import com.wu.money_transfer_service.entity.Transfer;
import com.wu.money_transfer_service.model.ReceiptResponse;
import com.wu.money_transfer_service.model.TransferRequest;
import com.wu.money_transfer_service.model.TransferResponse;
import com.wu.money_transfer_service.repository.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final TransferRepository transferRepository;
    private final ExchangeRateService exchangeRateService;
    private final ReceiptService receiptService;

    // Explicitly define the constructor
    public TransferService(
            TransferRepository transferRepository,
            ExchangeRateService exchangeRateService,
            ReceiptService receiptService) {
        this.transferRepository = transferRepository;
        this.exchangeRateService = exchangeRateService;
        this.receiptService = receiptService;
    }

    public TransferResponse processTransfer(TransferRequest request) {
        log.info("Processing transfer request: {}", request);

        // Generate a unique transfer ID
        String transferId = "TRX" + UUID.randomUUID().toString().substring(0, 8);

        // Calculate exchange rate and receiver amount
        double exchangeRate = exchangeRateService.getExchangeRate(
                request.getSenderCurrency(),
                request.getReceiverCurrency());

        double receiverAmount = exchangeRateService.calculateReceiverAmount(
                request.getAmount(),
                request.getSenderCurrency(),
                request.getReceiverCurrency());

        // Create and save the transfer entity
        Transfer transfer = Transfer.builder()
                .transferId(transferId)
                .senderAccountId(request.getSenderAccountId())
                .receiverAccountId(request.getReceiverAccountId())
                .senderAmount(request.getAmount())
                .senderCurrency(request.getSenderCurrency())
                .receiverAmount(receiverAmount)
                .receiverCurrency(request.getReceiverCurrency())
                .exchangeRate(exchangeRate)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now().now())
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);
        log.info("Transfer saved with ID: {}", savedTransfer.getTransferId());

        // Create and return the response
        return TransferResponse.builder()
                .transferId(transferId)
                .status("SUCCESS")
                .message("Transfer completed successfully.")
                .timestamp(ZonedDateTime.now())
                .build();
    }

    public ReceiptResponse getTransferReceipt(String transferId) {
        log.info("Getting receipt for transfer ID: {}", transferId);

        Transfer transfer = transferRepository.findByTransferId(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found with ID: " + transferId));

        return receiptService.generateReceipt(transfer);
    }
}
