package com.camilachangperes.process_pix_transaction.validator;

import com.camilachangperes.process_pix_transaction.validation.PixTransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PixTransactionValidatorTest {

    private PixTransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PixTransactionValidator();
    }

    @Test
    void deveValidarPixKeyEAmountCorretos() {
        assertDoesNotThrow(() -> validator.validatePixKey("chave@teste.com", 100L));
    }

    @Test
    void deveLancarExcecaoQuandoPixKeyNulaOuVazia() {
        assertThrows(IllegalArgumentException.class, () -> validator.validatePixKey(null, 100L));
        assertThrows(IllegalArgumentException.class, () -> validator.validatePixKey("", 100L));
    }

    @Test
    void deveLancarExcecaoQuandoAmountNuloOuMenorIgualZero() {
        assertThrows(IllegalArgumentException.class, () -> validator.validatePixKey("chave@teste.com", null));
        assertThrows(IllegalArgumentException.class, () -> validator.validatePixKey("chave@teste.com", 0L));
        assertThrows(IllegalArgumentException.class, () -> validator.validatePixKey("chave@teste.com", -10L));
    }


}
