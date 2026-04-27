package com.camilachangperes.process_pix_transaction.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FraudService {
// Simula uma verificação de fraude. Neste exemplo, qualquer transação acima de R$ 10.000 ou com chave Pix vazia é considerada suspeita
    public boolean isApproved(BigDecimal amount, String pixKey) {

        if (amount.compareTo(new BigDecimal("10000")) > 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return pixKey != null && !pixKey.isEmpty(); // Verifica se a chave Pix não é nula ou vazia
    }
}
