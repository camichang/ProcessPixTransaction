package com.camilachangperes.process_pix_transaction.controller;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.service.PixTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pix")
public class PixTransactionController {

    private PixTransactionService pixTransactionService;

    public PixTransactionController(PixTransactionService pixTransactionService) {
        this.pixTransactionService = pixTransactionService;
    }

    @PostMapping("/pay")
    public ResponseEntity<String> paypix(@Valid @RequestBody PixTransactionRequest request) {

        Transaction transaction = pixTransactionService.createdTransaction(request);

        if (transaction.getStatus().equals(StatusTransaction.REPROVED)
                || transaction.getStatus().equals(StatusTransaction.REPROVED_FRAUD)
                || transaction.getStatus().equals(StatusTransaction.REPROVED_ACCOUNT_BLOCKED)) {
            return ResponseEntity.badRequest().body(
                    "Pix transaction reproved: pixKey: " + request.getPixKey() + ", amount: " + request.getAmount());
        }

        return ResponseEntity.ok(
                "Pix transaction processed successfully: pixKey: " + request.getPixKey() + ", amount: " + request.getAmount());
    }
}
