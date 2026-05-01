package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.model.Account;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.camilachangperes.process_pix_transaction.utils.AccountUtils.loadAccountsFromFile;

@Service
public class AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    public AccountService() {
        List<Account> loadedAccounts = loadAccountsFromFile("accounts.json");
        for (Account account : loadedAccounts){
            accounts.put(account.getPixKey(), account);
        }
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
