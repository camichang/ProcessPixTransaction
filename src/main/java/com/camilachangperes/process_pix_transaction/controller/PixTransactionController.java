package com.camilachangperes.process_pix_transaction.controller;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pix")
public class PixTransactionController {

    @PostMapping("/pay")
    public ResponseEntity<String>
    paypix(@Valid @RequestBody PixTransactionRequest request){

        return ResponseEntity.ok(
                "Pix transaction processed successfully for id: " + request.getId() +
                ", pixKey: " + request.getPixKey() + ", amount: " + request.getAmount());
    }
}
