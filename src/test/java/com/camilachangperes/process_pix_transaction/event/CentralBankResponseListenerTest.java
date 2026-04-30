package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.dto.ClientTransactionStatusDTO;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CentralBankResponseListenerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private CentralBankResponseListener centralBankResponseListener;

    @BeforeEach
    void setUp() {
        centralBankResponseListener = new CentralBankResponseListener(
                transactionRepository,
                messagingTemplate
        );
    }

    @Test
    void deveProcessarRespostaDoBancoCentral() {
        Transaction transaction = new Transaction();
        transaction.setId("id123");
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(100L);
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.findById("id123")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseEvent event = new TransactionResponseEvent(
                "id123", "chave@teste.com", 100L, StatusTransaction.APPROVED);

        centralBankResponseListener.handleCentralBankResponseEvent(event);

        verify(transactionRepository).save(any(Transaction.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/pix-status"), any(ClientTransactionStatusDTO.class));
    }
}
