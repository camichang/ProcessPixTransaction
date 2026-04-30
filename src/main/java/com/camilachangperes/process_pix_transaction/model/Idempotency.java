package com.camilachangperes.process_pix_transaction.model;

import jakarta.persistence.*;

@Entity
public class Idempotency {

    @Id
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;


    @Column(name = "amount", nullable = false)
    private Long amount;

    public Idempotency(){
    }

    public Idempotency(String idempotencyKey, String transactionId, Long amount){
        this.idempotencyKey = idempotencyKey;
        this.transactionId = transactionId;
        this.amount = amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}
