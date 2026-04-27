package com.camilachangperes.process_pix_transaction.service;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.event.TransactionEvent;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PixTransactionServiceTest {
    private PixTransactionService pixTransactionService;
    private AccountService accountService;
    private FraudService fraudService;
    private TransactionRepository transactionRepository;
    private PixTransactionValidator pixTransactionValidator;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        fraudService = mock(FraudService.class);
        pixTransactionValidator = mock(PixTransactionValidator.class);
        transactionRepository = mock(TransactionRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        pixTransactionService = new PixTransactionService(
                accountService,
                fraudService,
                pixTransactionValidator,
                transactionRepository,
                eventPublisher
        );
    }

    @Test
    void deveReprovarTransacaoQuandoContaBloqueadaESalvarNoBanco() {
        PixTransactionRequest request = new PixTransactionRequest("chave2@banco.com", new BigDecimal("100.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(true);

       Transaction result = pixTransactionService.processPixTransaction((request));

         assertEquals("REPROVED", result.getStatus());

         verify(transactionRepository).save(any(Transaction.class));

    }

    @Test
    void deveCriarTransacaoQuandoSaldoSuficienteEContaNaoBloqueada() {
        PixTransactionRequest request = new PixTransactionRequest("chave3@banco.com", new BigDecimal("50.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);
        when(fraudService.isApproved(any(), any())).thenReturn(true);

        Transaction transaction = new Transaction();
        transaction.setId(java.util.UUID.randomUUID());
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = pixTransactionService.processPixTransaction(request);

        assertEquals(StatusTransaction.PENDING, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());

        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    void deveReprovarTransacaoQuandoFraudeDetectada() {
        PixTransactionRequest request = new PixTransactionRequest("chave2@banco.com", new BigDecimal("100.00"));

        when(accountService.hasSufficientBalance(request.getPixKey(), request.getAmount())).thenReturn(true);
        when(accountService.isAccountBlocked(request.getPixKey())).thenReturn(false);
        when(fraudService.isApproved(any(), any())).thenReturn(false);

        Transaction transaction = new Transaction();
        transaction.setId(java.util.UUID.randomUUID());
        transaction.setPixKey(request.getPixKey());
        transaction.setAmount(request.getAmount().toString());
        transaction.setStatus(StatusTransaction.PENDING);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = pixTransactionService.processPixTransaction(request);

        assertEquals(StatusTransaction.REPROVED_FRAUD, result.getStatus());
        assertEquals(request.getPixKey(), result.getPixKey());

        verify(transactionRepository, atLeast(2)).save(any(Transaction.class));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }



}


