package com.camilachangperes.process_pix_transaction.validation;

import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PixTransactionValidator {

    public void validatePixKey(String pixKey, BigDecimal amount) {
        if (pixKey == null || pixKey.isEmpty()) {
            throw new IllegalArgumentException("Pix key cannot be null or empty");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0.0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
