package com.camilachangperes.process_pix_transaction.model;

public class Account {

    private String pixKey;
    private String id;
    private Long balance;
    private boolean blocked;

    public Account() {}

    public Account(String pixKey, String id, Long balance, boolean blocked) {
        this.pixKey = pixKey;
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

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }


}
