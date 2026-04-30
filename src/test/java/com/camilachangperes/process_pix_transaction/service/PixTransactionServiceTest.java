package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.event.TransactionCreatedEvent;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.IdempotencyRepository;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PixTransactionServiceTest {
    private TransactionRepository transactionRepository;
    private PixTransactionValidator pixTransactionValidator;
    private AccountService accountService;
    private ApplicationEventPublisher eventPublisher;
    private IdempotencyRepository idempotencyRepository;
    private PixTransactionService pixTransactionService;
    private FraudService fraudService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        pixTransactionValidator = mock(PixTransactionValidator.class);
        accountService = mock(AccountService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        idempotencyRepository = mock(IdempotencyRepository.class);
        fraudService = mock(FraudService.class);

        pixTransactionService = new PixTransactionService(
                transactionRepository,
                pixTransactionValidator,
                accountService,
                eventPublisher,
                idempotencyRepository,
                fraudService
        );
    }

    @Test
    void deveReprovarTransacaoQuandoContaBloqueadaESalvarNoBanco() {
        PixTransactionRequest request = new PixTransactionRequest("chave2@banco.com",100L);
        String idempotencyKey = "key12345";

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction t = invocation.getArgument(0);
                    t.setId(UUID.randomUUID().toString());
                    return t;
                });

        Transaction result = pixTransactionService.createdTransaction(request, idempotencyKey);

        assertEquals(StatusTransaction.REPROVED_ACCOUNT_BLOCKED, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());
        assertNotNull(result.getId());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deveCriarTransacaoQuandoSaldoSuficienteEContaNaoBloqueada() {
        PixTransactionRequest request = new PixTransactionRequest("chave3@banco.com", 50L);

        String idempotencyKey = "key12345";

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(fraudService.isApproved(request.getAmount())).thenReturn(true);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction t = invocation.getArgument(0);
                    t.setId(UUID.randomUUID().toString());
                    return t;
                });

        Transaction result = pixTransactionService.createdTransaction(request, idempotencyKey);

        assertEquals(StatusTransaction.APPROVED, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());
        assertNotNull(result.getId());

        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void deveReprovarTransacaoQuandoSaldoDAContaIndisponivel() {
        LogCaptor logCaptor = LogCaptor.forClass(PixTransactionService.class);

        PixTransactionRequest request = new PixTransactionRequest("chave3@banco.com", 50L);
        String idempotencyKey = UUID.randomUUID().toString();

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(false);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = pixTransactionService.createdTransaction(request, idempotencyKey);

        assertEquals(StatusTransaction.REPROVED, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());

        assertTrue(logCaptor.getWarnLogs().stream()
                .anyMatch(msg -> msg.contains("Insufficient balance")));

        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionCreatedEvent.class));
    }


}


