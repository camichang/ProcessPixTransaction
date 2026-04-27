package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionEventListener {

    @Autowired
    TransactionRepository transactionRepository;

    @EventListener
    public void handleTransactionEvent(TransactionEvent event) {
        var transactionOpt = transactionRepository.findById(event.getId());
        if (transactionOpt.isPresent()) {
            var transaction = transactionOpt.get();
            transaction.setStatus(StatusTransaction.APPROVED);
            transactionRepository.save(transaction);
        } else {
            System.out.println("Transaction not found for ID: " + event.getId());
        }

        System.out.println("Received Transaction Event: " + event.getId() + " " +
                ", PixKey: " + event.getPixKey() + ", Amount: " + event.getAmount() + ", Status: " + event.getStatus());
    }
}
