package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.mock.CentralBankMock;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.service.FraudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionCreatedListenerTest {

    private TransactionRepository transactionRepository;
    private FraudService fraudService;
    private ApplicationEventPublisher eventPublisher;
    private CentralBankMock centralBankMock;
    private TransactionCreatedListener listener;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        fraudService = mock(FraudService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        centralBankMock = mock(CentralBankMock.class);

        listener = new TransactionCreatedListener(transactionRepository, centralBankMock);
    }

    @Test
    void deveEnviarEventoAoBancoCentralQuandoTransacaoEncontrada() {
        String id = UUID.randomUUID().toString();
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(100L);
        transaction.setStatus(StatusTransaction.APPROVED);

        TransactionCreatedEvent event = new TransactionCreatedEvent(
                id, transaction.getPixKey(), transaction.getAmount(), transaction.getStatus()
        );

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        listener.handleTransactionCreatedEvent(event);

        verify(centralBankMock).send(any(TransactionResponseEvent.class));
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoEncontrada() {
        String id = UUID.randomUUID().toString();
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                id,
                "chave@teste.com",
                50L,
                StatusTransaction.PENDING);

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> listener.handleTransactionCreatedEvent(event));
    }

    @Test
    void deveEnviarEventoComDadosCorretos() {
        String id = UUID.randomUUID().toString();
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(200L);
        transaction.setStatus(StatusTransaction.REPROVED);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        TransactionCreatedEvent event = new TransactionCreatedEvent(id, transaction.getPixKey(), transaction.getAmount(), transaction.getStatus());

        listener.handleTransactionCreatedEvent(event);

        ArgumentCaptor<TransactionResponseEvent> captor = ArgumentCaptor.forClass(TransactionResponseEvent.class);
        verify(centralBankMock).send(captor.capture());

        TransactionResponseEvent response = captor.getValue();
        assertEquals(transaction.getId(), response.id());
        assertEquals(transaction.getPixKey(), response.pixKey());
        assertEquals(transaction.getAmount(), response.amount());
        assertEquals(transaction.getStatus(), response.status());
    }


}

