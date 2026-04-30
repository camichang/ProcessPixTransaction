package com.camilachangperes.process_pix_transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PixTransactionRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String pixKey;

    @NotNull
    @Positive
    private Long amount;

    public PixTransactionRequest(String pixKey, Long amount) {
        this.pixKey = pixKey;
        this.amount = amount;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }



}
