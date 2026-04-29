package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

import java.math.BigDecimal;
import java.util.UUID;

public class CentralBankResponseEvent {

        private UUID id;
        private String pixKey;
        private BigDecimal amount;
        private StatusTransaction status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public StatusTransaction getStatus() {
        return status;
    }

    public void setStatus(StatusTransaction status) {
        this.status = status;
    }

    public void CentralBankResponseEvent(UUID id, String pixKey, BigDecimal amount, StatusTransaction status) {
        this.id = id;
        this.pixKey = pixKey;
        this.amount = amount;
        this.status = status;
    }



}
