package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    public AccountService() {
        // contas simuladas para teste
        accounts.put("pixKey1", new Account(java.util.UUID.randomUUID(), new BigDecimal("1000.00"), false));
        accounts.put("pixKey2", new Account(java.util.UUID.randomUUID(), new BigDecimal("500.00"), false));
        accounts.put("pixKey3", new Account(java.util.UUID.randomUUID(), new BigDecimal("200.00"), true)); // conta bloqueada
    }

    //verifica se tem saldo suficiente
    public boolean hasSufficientBalance(String pixKey, BigDecimal amount) {
        Account account = accounts.get(pixKey);
        return account != null && account.getBalance().compareTo(amount) >= 0;
    }

    //verifica se a conta esta bloqueada
    public boolean isAccountBlocked(String pixKey) {
        Account account = accounts.get(pixKey);
        return account != null && account.isBlocked();
    }
}
