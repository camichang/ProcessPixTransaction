package com.camilachangperes.process_pix_transaction.controller;

import com.camilachangperes.process_pix_transaction.dto.PixTransactionRequest;
import com.camilachangperes.process_pix_transaction.model.StatusTransaction;
import com.camilachangperes.process_pix_transaction.model.Transaction;
import com.camilachangperes.process_pix_transaction.service.PixTransactionService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PixTransactionControllerTest {

    @Mock
    private PixTransactionService pixTransactionService;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void deveCriarTransacaoComBadRequest() {
        given()
                .contentType("application/json")
                .header("idempotency_key", "key123")
                .body("{\"pixKey\":\"chave@teste.com\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(400)
                .body("pixKey", equalTo("chave@teste.com"))
                .body("amount", equalTo(100));
    }

    @Test
    void deveCriarTransacaoComSucesso() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(100L);
        transaction.setStatus(StatusTransaction.APPROVED);

        when(pixTransactionService.createdTransaction(any(PixTransactionRequest.class), eq("key123")))
                .thenReturn(transaction);

        given()
                .contentType("application/json")
                .header("idempotency_key", "key123")
                .body("{\"pixKey\":\"chave@teste.com\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(200)
                .body("pixKey", equalTo("chave@teste.com"))
                .body("amount", equalTo(100))
                .body("message", equalTo("Pix transaction processed successfully: pixKey: chave@teste.com, amount: 100"));
    }

    @Test
    void deveRetornarBadRequestComPayloadInvalido() {
        given()
                .contentType("application/json")
        .header("idempotency_key", "key123")
                .body("{\"amount\":100}")
        .when()
                .post("/pix/pay")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoIdempotencyKeyNaoForEnviada() {
        given()
                .contentType("application/json")
                .body("{\"pixKey\":\"chave@teste.com\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarMesmaTransacaoQuandoIdempotenciaKeyForDuplicada() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setPixKey("chave@teste.com");
        transaction.setAmount(100L);
        transaction.setStatus(StatusTransaction.APPROVED);

        when(pixTransactionService.createdTransaction(any(PixTransactionRequest.class), eq("key123")))
                .thenReturn(transaction);

        given()
                .contentType("application/json")
                .header("idempotency_key", "key123")
                .body("{\"pixKey\":\"chave@teste.com\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(200)
                .body("id", equalTo(transaction.getId()));

        // Segunda chamada com mesma chave
        given()
                .contentType("application/json")
                .header("idempotency_key", "key123")
                .body("{\"pixKey\":\"chave@teste.com\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(200)
                .body("id", equalTo(transaction.getId()));
    }
}
