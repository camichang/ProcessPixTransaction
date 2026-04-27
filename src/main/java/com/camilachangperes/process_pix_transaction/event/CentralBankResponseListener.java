package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class CentralBankResponseListener {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CentralBankResponseListener.class);

        @EventListener
        public void handleCentralBankResponseEvent(CentralBankResponseEvent event) {
            System.out.println("Received Central Bank Response Event: " + event.getId() + " " +
                    ", PixKey: " + event.getPixKey() + ", Amount: " + event.getAmount() + ", Status: " + event.getStatus());

            transactionRepository.findById(event.getId()).ifPresent(transaction -> {
                transaction.setStatus(event.getStatus());
                transactionRepository.save(transaction);
                logger.info("Updated transaction {} with status: {}", transaction.getId(), transaction.getStatus());

                messagingTemplate.convertAndSend("topic/pix-status", transaction);
            });
        }
}
