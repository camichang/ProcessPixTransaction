package com.camilachangperes.process_pix_transaction.integration;

import com.camilachangperes.process_pix_transaction.dto.ClientTransactionStatusDTO;
import com.camilachangperes.process_pix_transaction.event.TransactionResponseEvent;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.service.AccountService;
import com.camilachangperes.process_pix_transaction.service.FraudService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PixTransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private FraudService fraudService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        reset(messagingTemplate);
        reset(accountService);
    }

    @Test
    void fluxoCompletoHttpTransacaoAprovada() {
        String pixKey = "chave@teste.com";
        long amount = 100L;

        when(accountService.isAccountBlocked(pixKey)).thenReturn(false);
        when(accountService.hasSufficientBalance(pixKey, amount)).thenReturn(true);
        when(fraudService.isApproved(amount)).thenReturn(true);

        String transactionId =
                given()
                        .contentType(ContentType.JSON)
                        .header("idempotency_key", "key123")
                        .body("{\"pixKey\":\"chave@teste.com\",\"amount\":100}")
                .when()
                        .post("/pix/pay")
                .then()
                        .statusCode(200)
                        .extract()
                        .path("id");
        Transaction created = transactionRepository.findById(transactionId).orElseThrow();
                assertEquals("chave@teste.com", created.getPixKey());
                assertEquals(100L, created.getAmount());

        TransactionResponseEvent event = new TransactionResponseEvent(
                transactionId, "chave@teste.com", 100L, StatusTransaction.APPROVED
        );
        eventPublisher.publishEvent(event);

        Transaction updated = transactionRepository.findById(transactionId).orElseThrow();
        assertEquals(StatusTransaction.APPROVED, updated.getStatus());

        verify(messagingTemplate, times(2)).convertAndSend(
                eq("/topic/pix-status"),
                any(ClientTransactionStatusDTO.class)
        );
    }

    @Test
    void fluxoCompletoHttpTransacaoReprovadaPorSaldoInsuficiente() {
        String pixKey = "chave@teste.com";
        long amount = 100L;

        when(accountService.hasSufficientBalance(pixKey, amount)).thenReturn(false);
        when(accountService.isAccountBlocked(pixKey)).thenReturn(false);

        String transactionId =
                given()
                        .contentType(ContentType.JSON)
                        .header("idempotency_key", "key124")
                        .body("{\"pixKey\":\"" + pixKey + "\",\"amount\":" + amount + "}")
                        .when()
                        .post("/pix/pay")
                        .then()
                        .statusCode(400)
                        .extract()
                        .path("id");

        Transaction created = transactionRepository.findById(transactionId).orElseThrow();
        assertEquals(StatusTransaction.REPROVED, created.getStatus());
    }

    @Test
    void fluxoCompletoHttpTransacaoReprovadaPorContaBloqueada() {
        String pixKey = "chave@teste.com";
        long amount = 100L;

        when(accountService.isAccountBlocked(pixKey)).thenReturn(true);

        String transactionId =
                given()
                        .contentType(ContentType.JSON)
                        .header("idempotency_key", "key1225")
                        .body("{\"pixKey\":\"" + pixKey + "\",\"amount\":" + amount + "}")
                        .when()
                        .post("/pix/pay")
                        .then()
                        .statusCode(400)
                        .extract()
                        .path("id");

        Transaction created = transactionRepository.findById(transactionId).orElseThrow();
        assertEquals(StatusTransaction.REPROVED_ACCOUNT_BLOCKED, created.getStatus());
    }

    @Test
    void fluxoCompletoHttpTransacaoReprovadaPorFraude() {
        String pixKey = "chave@teste.com";
        long amount = 100L;

        when(accountService.isAccountBlocked(pixKey)).thenReturn(false);
        when(accountService.hasSufficientBalance(pixKey, amount)).thenReturn(true);
        when(fraudService.isApproved(amount)).thenReturn(false);

        String transactionId =
                given()
                        .contentType(ContentType.JSON)
                        .header("idempotency_key", "key126")
                        .body("{\"pixKey\":\"" + pixKey + "\",\"amount\":" + amount + "}")
                        .when()
                        .post("/pix/pay")
                        .then()
                        .statusCode(400)
                        .extract()
                        .path("id");

        Transaction created = transactionRepository.findById(transactionId).orElseThrow();
        assertEquals(StatusTransaction.REPROVED_FRAUD, created.getStatus());
    }
}
