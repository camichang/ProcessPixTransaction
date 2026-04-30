package com.camilachangperes.process_pix_transaction.controller;

import com.camilachangperes.process_pix_transaction.dto.ClientTransactionStatusDTO;
import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.service.PixTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
public class PixTransactionController {

    private final PixTransactionService pixTransactionService;

    public PixTransactionController(PixTransactionService pixTransactionService) {
        this.pixTransactionService = pixTransactionService;
    }

    @PostMapping("/pay")
    public ResponseEntity<ClientTransactionStatusDTO> paypix(
            @RequestHeader("idempotency_key") String idempotencyKey,
            @Valid @RequestBody PixTransactionRequest request) {

        Transaction transaction = pixTransactionService.createdTransaction(request, idempotencyKey);

        if (!transaction.getStatus().equals(StatusTransaction.APPROVED)){

            ClientTransactionStatusDTO dto = new ClientTransactionStatusDTO(
                    transaction.getId(),
                    transaction.getPixKey(),
                    transaction.getAmount(),
                    "Transaction was rejected. Please check your account status or contact support. "
            );
            return ResponseEntity.badRequest().body(dto);
        }

        ClientTransactionStatusDTO dto = new ClientTransactionStatusDTO(
                transaction.getId(),
                transaction.getPixKey(),
                transaction.getAmount(),
                "Pix transaction processed successfully: pixKey: " + transaction.getPixKey() + ", amount: " + transaction.getAmount()
        );
        return ResponseEntity.ok(dto);
    }
}
