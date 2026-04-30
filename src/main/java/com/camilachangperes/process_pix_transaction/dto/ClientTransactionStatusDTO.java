package com.camilachangperes.process_pix_transaction.dto;


public record ClientTransactionStatusDTO (
        String id,
        String pixKey,
        Long amount,
        String message
){}
