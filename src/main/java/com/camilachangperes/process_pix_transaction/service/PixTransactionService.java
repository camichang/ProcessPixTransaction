package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.entity.Transaction;
import com.camilachangperes.process_pix_transaction.event.TransactionEvent;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
    ApplicationEventPublisher eventPublisher;

    @Autowired
    TransactionEvent transactionEvent;

    @Value("${app.pix.transaction.topic}")
    private String topic;

    private static final Logger logger = LoggerFactory.getLogger(PixTransactionService.class);

    public void processPixTransaction(PixTransactionRequest request) {
        savePixTransaction(request.getPixKey(), request.getAmount()); //valida e salva no banco de dados

        var saved = savePixTransaction(request.getPixKey(), request.getAmount());

        eventPublisher.publishEvent(new TransactionEvent(
                saved.getId(),
                saved.getPixKey(),
                saved.getAmount(),
                saved.getStatus()
        )); //envia a mensagem para o topico do evento
    }

    private Transaction savePixTransaction(String pixKey, BigDecimal amount) {
        pixTransactionValidator.validatePixKey(pixKey, amount);

        Transaction transaction = new Transaction();
        transaction.setPixKey(pixKey);
        transaction.setAmount(amount.toString());
        transaction.setStatus("PENDING");

        var savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction processed successfully: {}", transaction.getId());

       return savedTransaction; //salva a transação no banco de dados e retorna a entidade salva

    }


}
