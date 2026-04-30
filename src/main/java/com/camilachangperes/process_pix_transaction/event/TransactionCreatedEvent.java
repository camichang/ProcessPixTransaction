package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

public record TransactionCreatedEvent (
        String id,
        String pixKey,
        Long amount,
        StatusTransaction status
){}
