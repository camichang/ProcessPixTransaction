package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.dto.ClientTransactionStatusDTO;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CentralBankMockTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private CentralBankResponseListener listener;

    @BeforeEach
    void setUp(){
        listener = new CentralBankResponseListener(
                transactionRepository,
                messagingTemplate
        );
    }

    @Test
    void deveProcessarRespostaAprovada() {
        Transaction transaction = new Transaction();
        transaction.setId("id123");
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(100L);
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.findById("id123")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseEvent event = new TransactionResponseEvent(
                "id123", "chave@teste.com", 100L, StatusTransaction.APPROVED);

        listener.handleCentralBankResponseEvent(event);

        verify(transactionRepository).save(any(Transaction.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/pix-status"),
                ArgumentMatchers.<Object>any());
    }

    @Test
    void deveProcessarRespostaReprovadaPorFraude() {
        Transaction transaction = new Transaction();
        transaction.setId("id123");
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(2000L);
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.findById("id123")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseEvent event = new TransactionResponseEvent(
                "id123", "chave@teste.com", 2000L, StatusTransaction.REPROVED_FRAUD);

        listener.handleCentralBankResponseEvent(event);

        verify(transactionRepository).save(any(Transaction.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/pix-status"),
                ArgumentMatchers.<Object>any());
    }

    @Test
    void naoDeveProcessarQuandoTransacaoNaoEncontrada() {
        when(transactionRepository.findById("id123")).thenReturn(Optional.empty());

        TransactionResponseEvent event = new TransactionResponseEvent(
                "id123", "chave@teste.com", 2000L, StatusTransaction.APPROVED);

        listener.handleCentralBankResponseEvent(event);

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(ClientTransactionStatusDTO.class));
    }
}
