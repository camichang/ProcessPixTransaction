package com.camilachangperes.process_pix_transaction.utils;

import com.camilachangperes.process_pix_transaction.model.Account;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AccountUtils {
    public static List<Account> loadAccountsFromFile(String resourceName) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = AccountUtils.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new RuntimeException("Arquivo não encontrado: " + resourceName);
            }
            List<Account> loadedAccounts = mapper.readValue(
                    is,
                    new TypeReference<>() {
                    }
            );
            for (Account account : loadedAccounts) {
                if (account.getId() == null || account.getId().isEmpty()) {
                    account.setId(RandomStringUtils.randomAlphanumeric(6));
                }
            }
            return loadedAccounts;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar contas do arquivo JSON", e);
        }
    }
}
