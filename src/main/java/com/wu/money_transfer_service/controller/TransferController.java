package com.wu.money_transfer_service.controller;


import com.wu.money_transfer_service.model.ErrorResponse;
import com.wu.money_transfer_service.model.ReceiptResponse;
import com.wu.money_transfer_service.model.TransferRequest;
import com.wu.money_transfer_service.model.TransferResponse;
import com.wu.money_transfer_service.service.ExchangeRateService;
import com.wu.money_transfer_service.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Transfer API", description = "API for money transfers between accounts")
public class TransferController {
    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @Operation(
            summary = "Initiate a money transfer",
            description = "Transfers money from sender to receiver account with currency conversion if needed"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer successful",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or insufficient balance",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.processTransfer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transferId}/receipt")
    @Operation(summary = "Get transfer receipt", description = "Retrieves a receipt for a completed transfer")
    public ResponseEntity<ReceiptResponse> getTransferReceipt(@PathVariable String transferId) {
        log.info("Received request for transfer receipt: {}", transferId);
        ReceiptResponse receipt = transferService.getTransferReceipt(transferId);
        return ResponseEntity.ok(receipt);
    }
}
