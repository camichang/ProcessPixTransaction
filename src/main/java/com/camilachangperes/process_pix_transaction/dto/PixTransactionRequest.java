package com.camilachangperes.process_pix_transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PixTransactionRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String pixKey;

    @NotNull
    @Positive
    private BigDecimal amount;

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PixTransactionRequest(String pixKey, BigDecimal amount) {
        this.pixKey = pixKey;
        this.amount = amount;
    }

}
