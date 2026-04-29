package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponseEvent (
        UUID id,
        String pixKey,
        BigDecimal amount,
        StatusTransaction status
){}
