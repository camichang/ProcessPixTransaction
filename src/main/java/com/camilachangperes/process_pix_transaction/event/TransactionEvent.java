package com.camilachangperes.process_pix_transaction.event;

import java.util.UUID;

public class TransactionEvent {

    private UUID id;
    private String pixKey;
    private String amount;
    private String status;

    public TransactionEvent(UUID id, String pixKey, String amount, String status) {
        this.id = id;
        this.pixKey = pixKey;
        this.amount = amount;
        this.status = status;
    }

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
