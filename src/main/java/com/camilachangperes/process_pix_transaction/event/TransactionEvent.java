package com.camilachangperes.process_pix_transaction.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TransactionEvent {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "pix_key", nullable = false)
    private String pixKey;

    @Column(name = "amount", nullable = false)
    private String amount;


    @Column(name = "status", nullable = false)
    private String status;

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
