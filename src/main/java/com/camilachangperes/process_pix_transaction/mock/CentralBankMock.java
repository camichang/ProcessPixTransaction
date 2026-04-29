package com.camilachangperes.process_pix_transaction.mock;

import com.camilachangperes.process_pix_transaction.event.TransactionResponseEvent;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CentralBankMock {

    private final ApplicationEventPublisher eventPublisher;

    public CentralBankMock(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void send(TransactionResponseEvent event) {
        StatusTransaction bankStatus = event.status();

        if (bankStatus == StatusTransaction.PENDING){
            bankStatus = event.amount().compareTo(new BigDecimal("1000")) > 0
                    ? StatusTransaction.REPROVED
                    : StatusTransaction.APPROVED;
        }

        eventPublisher.publishEvent(new TransactionResponseEvent(
                event.id(),
                event.pixKey(),
                event.amount(),
                bankStatus
        ));
    }
}
