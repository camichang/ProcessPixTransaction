package com.camilachangperes.process_pix_transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FraudService {


    private static final Logger logger = LoggerFactory.getLogger(FraudService.class);

    // Simula uma verificação de fraude. Neste exemplo, qualquer transação acima de R$ 10.000 ou com chave Pix vazia é considerada suspeita
    public boolean isApproved(BigDecimal amount) {

        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            logger.warn("Fraud detected: Transaction amound exceeds limit. Amount {}", amount);
            return false;
        }
        return true;
    };
}
