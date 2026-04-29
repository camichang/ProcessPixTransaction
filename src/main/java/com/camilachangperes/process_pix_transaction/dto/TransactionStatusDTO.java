package com.camilachangperes.process_pix_transaction.dto;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionStatusDTO (
        UUID id,
        String pixKey,
        BigDecimal amount,
        StatusTransaction status,
        String message
){}
