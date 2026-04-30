package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;

public class TransactionEvent {

    private String id;
    private String pixKey;
    private Long amount;
    private StatusTransaction status;

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public StatusTransaction getStatus() {
        return status;
    }

    public void setStatus(StatusTransaction status) {
        this.status = status;
    }

    public TransactionEvent(String id, String pixKey, Long amount, StatusTransaction status) {
        this.id = id;
        this.pixKey = pixKey;
        this.amount = amount;
        this.status = status;

    }

}