package com.camilachangperes.process_pix_transaction.model;

import jakarta.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "pix_key", nullable = false)
    private String pixKey;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "status", nullable = false)
    private StatusTransaction status;

    public Transaction(){
    }

    public StatusTransaction getStatus() {
        return status;
    }

    public void setStatus(StatusTransaction status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

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
}
