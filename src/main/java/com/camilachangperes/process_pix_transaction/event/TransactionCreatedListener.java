package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.mock.CentralBankMock;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.service.FraudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionCreatedListener {

    private final FraudService fraudService;
    private final TransactionRepository transactionRepository;
    private final CentralBankMock centralBankMock;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionCreatedListener(FraudService fraudService,
                                      TransactionRepository transactionRepository,
                                      CentralBankMock centralBankMock,
                                      ApplicationEventPublisher eventPublisher) {
        this.fraudService = fraudService;
        this.transactionRepository = transactionRepository;
        this.centralBankMock = centralBankMock;
        this.eventPublisher = eventPublisher;
    }

    private static final Logger logger = LoggerFactory.getLogger(TransactionCreatedListener.class);

    @EventListener
    public void handleTransactionCreatedEvent(TransactionCreatedEvent event) {

        Transaction transaction = transactionRepository.findById(event.id())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        //verifica se a transacao foi reprovada por bloqueio de conta, caso seja,
        // um evento é publicado para o MQ e a transacao nao é processada pelo serviço de fraude
        if (transaction.getStatus() == StatusTransaction.REPROVED_ACCOUNT_BLOCKED
                || transaction.getStatus() == StatusTransaction.REPROVED) {
            logger.warn("Transaction blocked by account status: " + transaction.getStatus());

            centralBankMock.send(new TransactionResponseEvent(
                    transaction.getId(),
                    transaction.getPixKey(),
                    new BigDecimal(transaction.getAmount()),
                    transaction.getStatus())
            );
            return;
        }


        //verifica se a transacao é aprovada ou reprovada pelo serviço de fraude,
        // caso seja reprovada a transacao é atualizada para o status REPROVED_FRAUD e um evento é publicado para o MQ
        if (transaction.getStatus() == StatusTransaction.PENDING) {
            boolean approved = fraudService.isApproved(event.amount());
            transaction.setStatus(approved ? StatusTransaction.APPROVED : StatusTransaction.REPROVED_FRAUD);
            Transaction saved = transactionRepository.save(transaction);

            logger.warn("Transaction processed by Fraud: " + saved.getId() + ", Status: " + saved.getStatus());

            centralBankMock.send(new TransactionResponseEvent(
                    saved.getId(),
                    saved.getPixKey(),
                    event.amount(),
                    saved.getStatus())
            );
        };
    }
}



