package com.camilachangperes.process_pix_transaction.event;

import com.camilachangperes.process_pix_transaction.mock.CentralBankMock;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.service.FraudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionCreatedListenerTest {
    @Mock
    private FraudService fraudService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransactionCreatedListener listener;

    @Mock
    private CentralBankMock centralBankMock;

    @Mock
    private Logger logger;

    @Test
    void deveReprovarTransacaoQuandoFraudeDetectada() {
        // dado um evento inicial
        UUID id = UUID.randomUUID();
        TransactionCreatedEvent event = new TransactionCreatedEvent(id, "chave@teste.com", new BigDecimal("100.00"), StatusTransaction.PENDING);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setPixKey(event.pixKey());
        transaction.setAmount(event.amount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation ->{
            Transaction t = invocation.getArgument(0);
            t.setId(id);
            return t;
        });
        when(fraudService.isApproved(event.amount())).thenReturn(false);

        // quando o listener processa
        listener.handleTransactionCreatedEvent(event);

        // então a transação deve ser salva com status REPROVED_FRAUD
        assertEquals(StatusTransaction.REPROVED_FRAUD, transaction.getStatus());

        verify(transactionRepository).save(transaction);
        verify(eventPublisher).publishEvent(any(TransactionResponseEvent.class)); // e o evento atualizado deve ser publicado
    }


    @Test
    void deveAprovarTransacaoQuandoFraudeNaoDetectada() {
        UUID id = UUID.randomUUID();
        TransactionCreatedEvent event = new TransactionCreatedEvent(id, "chave@teste.com", new BigDecimal("50.00"), StatusTransaction.PENDING);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setPixKey(event.pixKey());
        transaction.setAmount(event.amount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation ->{
            Transaction t = invocation.getArgument(0);
            t.setId(id);
            return t;
        });
        when(fraudService.isApproved(event.amount())).thenReturn(true);

        listener.handleTransactionCreatedEvent(event);

        assertEquals(StatusTransaction.APPROVED, transaction.getStatus());
        verify(transactionRepository).save(transaction);
        verify(eventPublisher).publishEvent(any(TransactionEvent.class));
    }
}

