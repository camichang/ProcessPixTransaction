package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.event.TransactionEvent;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
    FraudService fraudService;

    @Autowired
    AccountService accountService;

    @Value("${app.pix.transaction.topic}")
    private String topic;

    private static final Logger logger = LoggerFactory.getLogger(PixTransactionService.class);

    @Autowired
    public PixTransactionService(
            AccountService accountService,
            FraudService fraudService,
            PixTransactionValidator pixTransactionValidator,
            TransactionRepository transactionRepository,
            ApplicationEventPublisher eventPublisher){

        this.accountService = accountService;
        this.fraudService = fraudService;
        this.pixTransactionValidator = pixTransactionValidator;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    public Transaction processPixTransaction(PixTransactionRequest request) {
        // Valida os dados
        pixTransactionValidator.validatePixKey(request.getPixKey(), request.getAmount());

        //verifica se tem saldo na conta ou se esta bloqueada, caso uma das condicoes seja atendida a transacao é reprovada
        if (!accountService.hasSufficientBalance(request.getPixKey(), request.getAmount()) || accountService.isAccountBlocked(request.getPixKey())) {

            Transaction failed = new Transaction();
            failed.setPixKey(request.getPixKey());
            failed.setAmount(request.getAmount().toString());
            failed.setStatus(StatusTransaction.REPROVED);
            transactionRepository.save(failed);

            logger.warn("Transaction reproved: Insufficient balance for PixKey {}. Amount requested: {}", request.getPixKey(), request.getAmount());
            return failed;
        }

        // Cria a transação inicial com status PENDING
        Transaction transaction = new Transaction();
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        Transaction saved = transactionRepository.save(transaction);
        logger.info("Transaction created with ID: {} and status: {}", saved.getId(), saved.getStatus());

        // Verifica fraude usando FraudService
        boolean approved = fraudService.isApproved(request.getAmount(), request.getPixKey());
        if (!approved) {
            saved.setStatus(StatusTransaction.REPROVED_FRAUD);
            transactionRepository.save(saved);

            eventPublisher.publishEvent(new TransactionEvent(
                    saved.getId(),
                    saved.getPixKey(),
                    request.getAmount(),
                    saved.getStatus()
            ));
            logger.warn("Transaction {} reproved by fraud check. PixKey={}, Amount={}", saved.getId(), saved.getPixKey(), saved.getAmount());
            return saved;
        }

        // Se aprovado, publica evento para o MQ
        eventPublisher.publishEvent(new TransactionEvent(
                saved.getId(),
                saved.getPixKey(),
                request.getAmount(),
                saved.getStatus()
        ));
        logger.info("Transaction {} approved by fraud check and published to MQ", saved.getId());
        return saved;
    }
}

