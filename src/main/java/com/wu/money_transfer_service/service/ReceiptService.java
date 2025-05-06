package com.wu.money_transfer_service.service;

import com.wu.money_transfer_service.entity.Transfer;
import com.wu.money_transfer_service.model.ReceiptResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private static final Logger log = LoggerFactory.getLogger(ReceiptService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    /**
     * Generates a receipt for a completed transfer
     *
     * @param transfer The completed transfer entity
     * @return A receipt response with transfer details
     */
    public ReceiptResponse generateReceipt(Transfer transfer) {
        log.info("Generating receipt for transfer: {}", transfer.getTransferId());

        // Create a new instance without using builder
        ReceiptResponse response = new ReceiptResponse();
        response.setReceiptId("RCP" + transfer.getTransferId().substring(3));
        response.setTransferId(transfer.getTransferId());
        response.setSenderAccountId(transfer.getSenderAccountId());
        response.setReceiverAccountId(transfer.getReceiverAccountId());
        response.setSenderAmount(transfer.getSenderAmount());
        response.setSenderCurrency(transfer.getSenderCurrency());
        response.setReceiverAmount(transfer.getReceiverAmount());
        response.setReceiverCurrency(transfer.getReceiverCurrency());
        response.setExchangeRate(transfer.getExchangeRate());
        response.setStatus(transfer.getStatus());
        response.setTimestamp(formatTimestamp(transfer.getTimestamp()));

        return response;
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            return null;
        }

        // Use a formatter that doesn't require zone information
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    // Alternative method if you need to work with ZonedDateTime
    private String formatZonedTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            return null;
        }

        // Convert LocalDateTime to ZonedDateTime by adding a zone
        ZonedDateTime zonedDateTime = timestamp.atZone(ZoneId.systemDefault());

        // Use a formatter that includes zone information
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return zonedDateTime.format(formatter);
    }

}
