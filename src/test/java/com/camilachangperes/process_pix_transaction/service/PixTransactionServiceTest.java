package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.event.TransactionEvent;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import nl.altindag.log.LogCaptor;

public class PixTransactionServiceTest {
    private TransactionRepository transactionRepository;
    private PixTransactionValidator pixTransactionValidator;
    private AccountService accountService;
    private ApplicationEventPublisher eventPublisher;
    private PixTransactionService pixTransactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        pixTransactionValidator = mock(PixTransactionValidator.class);
        accountService = mock(AccountService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        pixTransactionService = new PixTransactionService(
                transactionRepository,
                pixTransactionValidator,
                accountService,
                eventPublisher
        );
    }

    @Test
    void deveReprovarTransacaoQuandoContaBloqueadaESalvarNoBanco() {
        PixTransactionRequest request = new PixTransactionRequest("chave2@banco.com", new BigDecimal("100.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(true);

        Transaction result = pixTransactionService.createdTransaction((request));

        assertEquals("REPROVED", result.getStatus());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deveReprovarTransacaoQuandoSaldoIndisponivel() {
        PixTransactionRequest request = new PixTransactionRequest("chave2@banco.com", new BigDecimal("100.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(false);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);

        Transaction result = pixTransactionService.createdTransaction((request));

        assertEquals("REPROVED", result.getStatus().toString());

        verify(transactionRepository).save(any(Transaction.class));

    }

    @Test
    void deveCriarTransacaoQuandoSaldoSuficienteEContaNaoBloqueada() {
        PixTransactionRequest request = new PixTransactionRequest("chave3@banco.com", new BigDecimal("50.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);

        Transaction transaction = new Transaction();
        transaction.setId(java.util.UUID.randomUUID());
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = pixTransactionService.createdTransaction(request);

        assertEquals(StatusTransaction.PENDING, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());

        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    void deveReprovarTransacaoQuandoSaldoDAContaIndisponivel() {
        LogCaptor logCaptor = LogCaptor.forClass(PixTransactionService.class);

        PixTransactionRequest request = new PixTransactionRequest("chave3@banco.com", new BigDecimal("50.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(false);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);

        Transaction transaction = new Transaction();
        transaction.setId(java.util.UUID.randomUUID());
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = pixTransactionService.createdTransaction(request);

        assertEquals(StatusTransaction.REPROVED, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());

        assertTrue(logCaptor.getWarnLogs().stream()
                .anyMatch(msg -> msg.contains("Insufficient balance")));

        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }


}


