package com.camilachangperes.process_pix_transaction.controller;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.service.PixTransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pix")
public class PixTransactionController {

    @Autowired
    private PixTransactionService pixTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<String>
    paypix(@Valid @RequestBody PixTransactionRequest request){

        pixTransactionService.processPixTransaction(request);

        return ResponseEntity.ok(
                "Pix transaction processed successfully: pixKey: " + request.getPixKey() + ", amount: " + request.getAmount());
    }
}
