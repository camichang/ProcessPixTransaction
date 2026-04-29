package com.camilachangperes.process_pix_transaction.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private UUID id;
    private BigDecimal balance;
    private boolean blocked;

    public Account(UUID id, BigDecimal balance, boolean blocked) {
        this.id = id;
        this.balance = balance;
        this.blocked = blocked;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }


}
