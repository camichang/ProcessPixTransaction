package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

public record TransactionResponseEvent (
        String id,
        String pixKey,
        Long amount,
        StatusTransaction status
){}
