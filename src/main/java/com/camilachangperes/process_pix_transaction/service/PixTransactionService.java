package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.event.TransactionCreatedEvent;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class PixTransactionService {

    private final TransactionRepository transactionRepository;
    private final PixTransactionValidator pixTransactionValidator;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(PixTransactionService.class);

    public PixTransactionService(TransactionRepository transactionRepository,
                                 PixTransactionValidator pixTransactionValidator,
                                 AccountService accountService,
                                 ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.pixTransactionValidator = pixTransactionValidator;
        this.accountService = accountService;
        this.eventPublisher = eventPublisher;
    }

    public Transaction createdTransaction(PixTransactionRequest request) {
        // Valida os dados
        pixTransactionValidator.validatePixKey(request.getPixKey(), request.getAmount());

        //verifica se tem saldo na conta ou se esta bloqueada, caso uma das condicoes seja atendida a transacao é reprovada
        if (accountService.isAccountBlocked(request.getPixKey())){
            Transaction failed = new Transaction();
            failed.setPixKey(request.getPixKey());
            failed.setAmount(request.getAmount().toString());
            failed.setStatus(StatusTransaction.REPROVED_ACCOUNT_BLOCKED);
            transactionRepository.save(failed);

            logger.warn("Account is blocked for PixKey={}", request.getPixKey());

            eventPublisher.publishEvent(new TransactionCreatedEvent(
                    failed.getId(),
                    failed.getPixKey(),
                    request.getAmount(),
                    failed.getStatus()
            ));

            return failed;
        }

       if (!accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())) {
            Transaction failed = new Transaction();
            failed.setPixKey(request.getPixKey());
            failed.setAmount(request.getAmount().toString());
            failed.setStatus(StatusTransaction.REPROVED);
            transactionRepository.save(failed);

            logger.warn("Insufficient balance for PixKey={}, Amount={}", request.getPixKey(), request.getAmount());

            eventPublisher.publishEvent(new TransactionCreatedEvent(
                    failed.getId(),
                    failed.getPixKey(),
                    request.getAmount(),
                    failed.getStatus()
            ));

            return failed;
        }

        Transaction transaction = new Transaction();
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        Transaction saved = transactionRepository.save(transaction);
        logger.info("Transaction created with ID: {} and status: {}", saved.getId(), saved.getStatus());

        eventPublisher.publishEvent(new TransactionCreatedEvent(
                saved.getId(),
                saved.getPixKey(),
                request.getAmount(),
                saved.getStatus()
        ));

        return saved;
    }
}

