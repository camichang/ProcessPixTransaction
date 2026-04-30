package com.camilachangperes.process_pix_transaction.model;

public class Account {

    private String id;
    private Long balance;
    private boolean blocked;

    public Account(String id, Long balance, boolean blocked) {
        this.id = id;
        this.balance = balance;
        this.blocked = blocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }


}
