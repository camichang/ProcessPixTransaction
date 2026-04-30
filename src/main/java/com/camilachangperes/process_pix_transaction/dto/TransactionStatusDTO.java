package com.camilachangperes.process_pix_transaction.dto;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

public record TransactionStatusDTO (
        String id,
        String pixKey,
        Long amount,
        StatusTransaction status,
        String message
){}
