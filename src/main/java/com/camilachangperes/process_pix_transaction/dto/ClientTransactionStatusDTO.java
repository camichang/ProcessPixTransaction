package com.camilachangperes.process_pix_transaction.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record ClientTransactionStatusDTO (
        UUID id,
        String pixKey,
        BigDecimal amount,
        String message
){}
