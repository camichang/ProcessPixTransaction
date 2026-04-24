package com.camilachangperes.process_pix_transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PixTransactionRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String id;

    @NotNull
    @Size(min = 1, max = 255)
    private String pixKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @NotNull
    @Positive
    private BigDecimal amount;


}
