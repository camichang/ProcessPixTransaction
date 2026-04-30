package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.model.Account;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    public AccountService() {
        // contas simuladas para teste
        accounts.put("teste1@teste.com", new Account(UUID.randomUUID().toString(), 30000L, false));
        accounts.put("123456789ab", new Account(UUID.randomUUID().toString(), 500L, false));
        accounts.put("pixKey3", new Account(UUID.randomUUID().toString(), 200L, true)); // conta bloqueada
    }

    //verifica se tem saldo suficiente
    public boolean hasSufficientBalance(String pixKey, Long amount) {
        Account account = accounts.get(pixKey);
        return account != null
                && amount.compareTo(0L) > 0
                && account.getBalance().compareTo(amount) >= 0;
    }

    //verifica se a conta esta bloqueada
    public boolean isAccountBlocked(String pixKey) {
        Account account = accounts.get(pixKey);
        return account != null && account.isBlocked();
    }
}
