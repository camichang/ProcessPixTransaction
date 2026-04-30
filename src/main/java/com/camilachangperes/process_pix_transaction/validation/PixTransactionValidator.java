package com.camilachangperes.process_pix_transaction.validation;

import org.springframework.stereotype.Component;

@Component
public class PixTransactionValidator {

    public void validatePixKey(String pixKey, Long amount) {
        if (pixKey == null || pixKey.isEmpty()) {
            throw new IllegalArgumentException("Pix key cannot be null or empty");
        }

        if (amount == null || amount.compareTo(0L) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
