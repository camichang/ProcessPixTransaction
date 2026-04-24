package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.entity.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PixTransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PixTransactionValidator pixTransactionValidator;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.pix.transaction.topic}")
    private String topic;

    public void processPixTransaction(PixTransactionRequest request) {
        savePixTransaction(request.getPixKey(), request.getAmount()); //valida e salva no banco de dados

        kafkaTemplate.send(topic, request); //envia a transação para o Kafka

    }

    private Transaction savePixTransaction(String pixKey, BigDecimal amount) {
        pixTransactionValidator.validatePixKey(pixKey, amount);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setPixKey(pixKey);
        transaction.setAmount(amount.toString());
        transaction.setStatus("PENDING");

       return transactionRepository.save(transaction); //salva a transação no banco de dados e retorna a entidade salva

    }


}
