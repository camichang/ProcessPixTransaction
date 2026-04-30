package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.dto.ClientTransactionStatusDTO;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class CentralBankResponseListener {

    private final TransactionRepository transactionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CentralBankResponseListener.class);

    public CentralBankResponseListener(TransactionRepository transactionRepository,
                                       SimpMessagingTemplate messagingTemplate) {
        this.transactionRepository = transactionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleCentralBankResponseEvent(TransactionResponseEvent event) {

        transactionRepository.findById(event.id()).ifPresent(transaction -> {
            transaction.setStatus(event.status());
            Transaction saved = transactionRepository.save(transaction);

            String message;

            if (saved.getStatus() == StatusTransaction.REPROVED_ACCOUNT_BLOCKED
                    || saved.getStatus() == StatusTransaction.REPROVED_FRAUD
                    || saved.getStatus() == StatusTransaction.REPROVED) {
                message = "Transaction was rejected. Please check your account status or contact support.";
            } else {
                message = "Transaction was successful.";
            }

            logger.info("Updated transaction {} with status: {}", transaction.getId(), transaction.getStatus());

            messagingTemplate.convertAndSend("/topic/pix-status", new ClientTransactionStatusDTO(
                    saved.getId(),
                    saved.getPixKey(),
                    event.amount(),
                    message
                    ));

            logger.info("Central Bank response processed: {}, Status: {}", saved.getId(), saved.getStatus());
        });
    }
}
