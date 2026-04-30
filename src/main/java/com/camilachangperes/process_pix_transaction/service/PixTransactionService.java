package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.event.TransactionCreatedEvent;
import com.camilachangperes.process_pix_transaction.model.Idempotency;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.IdempotencyRepository;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PixTransactionService {

    private final TransactionRepository transactionRepository;
    private final PixTransactionValidator pixTransactionValidator;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;
    private final IdempotencyRepository idempotencyRepository;
    private final FraudService fraudService;

    private static final Logger logger = LoggerFactory.getLogger(PixTransactionService.class);

    public PixTransactionService(TransactionRepository transactionRepository,
                                 PixTransactionValidator pixTransactionValidator,
                                 AccountService accountService,
                                 ApplicationEventPublisher eventPublisher,
                                 IdempotencyRepository idempotencyRepository,
                                 FraudService fraudService) {
        this.transactionRepository = transactionRepository;
        this.pixTransactionValidator = pixTransactionValidator;
        this.accountService = accountService;
        this.eventPublisher = eventPublisher;
        this.idempotencyRepository = idempotencyRepository;
        this.fraudService = fraudService;
    }

    public Transaction createdTransaction(PixTransactionRequest request, String idempotencyKey) {
        Optional<Idempotency> existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            logger.warn("Duplicate idempotency key detected: {}", idempotencyKey);
            Transaction transaction = transactionRepository.findById(existing.get().getTransactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            eventPublisher.publishEvent(new TransactionCreatedEvent(
                    transaction.getId(),
                    transaction.getPixKey(),
                    request.getAmount(),
                    transaction.getStatus()
            ));
            return transaction;
        }

        pixTransactionValidator.validatePixKey(request.getPixKey(), request.getAmount());

        if (accountService.isAccountBlocked(request.getPixKey())) {
            return saveTransactionReproved(request, StatusTransaction.REPROVED_ACCOUNT_BLOCKED,
                    "Account is blocked for PixKey=" + request.getPixKey(), idempotencyKey);
        }

        if (!accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())) {
            return saveTransactionReproved(request, StatusTransaction.REPROVED,
                    "Insufficient balance for PixKey=" + request.getPixKey() +
                            ", Amount=" + request.getAmount(), idempotencyKey);
        }

        boolean approved = fraudService.isApproved(request.getAmount());
        StatusTransaction status = approved ? StatusTransaction.APPROVED : StatusTransaction.REPROVED_FRAUD;

        return saveTransaction(request, status, idempotencyKey);
    }


    private Transaction saveTransaction(
            PixTransactionRequest request,
            StatusTransaction status,
            String idempotencyKey) {


        Transaction transaction = new Transaction();
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(status);

        Transaction saved = transactionRepository.save(transaction);
        idempotencyRepository.save(new Idempotency(idempotencyKey, saved.getId(), saved.getAmount()));

        logger.info("Transaction created with ID: {} and status: {}", saved.getId(), saved.getStatus());

        eventPublisher.publishEvent(new TransactionCreatedEvent(
                saved.getId(),
                saved.getPixKey(),
                request.getAmount(),
                saved.getStatus()
        ));

        return saved;
    }

    private Transaction saveTransactionReproved(
            PixTransactionRequest request,
            StatusTransaction status,
            String logMessage,
            String idempotencyKey) {

        logger.warn(logMessage);

        return saveTransaction(request, status, idempotencyKey);
    }
}

