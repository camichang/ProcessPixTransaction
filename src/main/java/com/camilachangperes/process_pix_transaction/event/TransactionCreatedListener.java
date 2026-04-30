package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.mock.CentralBankMock;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionCreatedListener {

    private final TransactionRepository transactionRepository;
    private final CentralBankMock centralBankMock;


    public TransactionCreatedListener(TransactionRepository transactionRepository,
                                      CentralBankMock centralBankMock) {
        this.transactionRepository = transactionRepository;
        this.centralBankMock = centralBankMock;
    }

    private static final Logger logger = LoggerFactory.getLogger(TransactionCreatedListener.class);

    @EventListener
    public void handleTransactionCreatedEvent(TransactionCreatedEvent event) {

        Transaction transaction = transactionRepository.findById(event.id())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            logger.info("Transaction received by listener: {} with status {} ", transaction.getId(), transaction.getStatus());

            centralBankMock.send(new TransactionResponseEvent(
                    transaction.getId(),
                    transaction.getPixKey(),
                    transaction.getAmount(),
                    transaction.getStatus())
            );
        }
}



