package com.camilachangperes.process_pix_transaction.controller;

import com.camilachangperes.process_pix_transaction.repository.IdempotencyRepository;
import com.camilachangperes.process_pix_transaction.repository.TransactionRepository;
import com.camilachangperes.process_pix_transaction.service.PixTransactionService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PixTransactionControllerTest {

    @Autowired
    private PixTransactionService pixTransactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        transactionRepository.deleteAll();
        idempotencyRepository.deleteAll();
    }

    @Test
    void deveCriarTransacaoComBadRequest() {
        given()
                .contentType("application/json")
                .header("idempotency_key", "key1234")
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
        String pixKey = "123456789ab";
        long amount = 100;

        given()
                .contentType("application/json")
                .header("idempotency_key", "key125")
                .body("{\"pixKey\":\"123456789ab\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(200)
                .body("pixKey", equalTo(pixKey))
                .body("amount", equalTo(100))
                .body("message", equalTo("Pix transaction processed successfully: pixKey: " + pixKey + ", amount: " + amount));
    }

    @Test
    void deveRetornarBadRequestComPayloadInvalido() {
        given()
                .contentType("application/json")
        .header("idempotency_key", "key127")
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
        String firstId = given()
                .contentType("application/json")
                .header("idempotency_key", "key123")
                .body("{\"pixKey\":\"123456789ab\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Segunda chamada com mesma chave
        String secondId = given()
                .contentType("application/json")
                .header("idempotency_key", "key123")
                .body("{\"pixKey\":\"123456789ab\",\"amount\":100}")
        .when()
                .post("/pix/pay")
        .then()
                .statusCode(200)
                .extract()
                .path("id");

        assertEquals(firstId, secondId, "Transações devem ser iguais para a mesma chave de idempotência");
    }
}
