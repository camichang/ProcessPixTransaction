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
        accounts.put("teste1@teste.com", new Account(java.util.UUID.randomUUID(), new BigDecimal("30000.00"), false));
        accounts.put("123456789ab", new Account(java.util.UUID.randomUUID(), new BigDecimal("500.00"), false));
        accounts.put("pixKey3", new Account(java.util.UUID.randomUUID(), new BigDecimal("200.00"), true)); // conta bloqueada
    }

    //verifica se tem saldo suficiente
    public boolean hasSufficientBalance(String pixKey, BigDecimal amount) {
        Account account = accounts.get(pixKey);
        return account != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && account.getBalance().compareTo(amount) >= 0;
    }

    //verifica se a conta esta bloqueada
    public boolean isAccountBlocked(String pixKey) {
        Account account = accounts.get(pixKey);
        return account != null && account.isBlocked();
    }
}
