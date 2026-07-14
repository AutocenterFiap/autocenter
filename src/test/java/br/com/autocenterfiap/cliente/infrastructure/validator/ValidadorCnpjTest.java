package br.com.autocenterfiap.cliente.infrastructure.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CNPJ Validator - Testes Unitários")
class ValidadorCnpjTest {

    private ValidadorCnpj validator;

    @BeforeEach
    void setUp() {
        validator = new ValidadorCnpj();
    }

    @Test
    void deveValidarCnpjValido() {
        String cnpjValido = "11222333000181"; // CNPJ válido de teste
        boolean resultado = validator.validar(cnpjValido);
        assertTrue(resultado, "CNPJ válido deve ser aceito");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11222333000181",
            "34028316000103",
            "60701190000104"
    })
    void deveValidarMultiplosCnpjsValidos(String cnpj) {
        assertTrue(validator.validar(cnpj), "CNPJ " + cnpj + " deveria ser válido");
    }

    @Test
    void deveRejeitarCnpjComTodosDigitosIguais() {
        assertFalse(validator.validar("11111111111111"));
        assertFalse(validator.validar("00000000000000"));
        assertFalse(validator.validar("99999999999999"));
    }

    @Test
    void deveRejeitarCnpjNulo() {
        assertFalse(validator.validar(null));
    }

    @Test
    void deveRejeitarCnpjComTamanhoIncorreto() {
        assertFalse(validator.validar("1122233300018"));   // muito curto
        assertFalse(validator.validar("112223330001811")); // muito longo
        assertFalse(validator.validar(""));                // vazio
    }

    @Test
    void deveRejeitarCnpjComCaracteresNaoNumericos() {
        assertFalse(validator.validar("11.222.333/0001-81")); // com formatação
        assertFalse(validator.validar("1122233300018X"));     // com letras
        assertFalse(validator.validar("11 222 333 0001 81")); // com espaços
    }

    @Test
    void deveRejeitarCnpjComDigitoVerificadorIncorreto() {
        String cnpjInvalido = "11222333000100"; // dígito verificador errado
        boolean resultado = validator.validar(cnpjInvalido);
        assertFalse(resultado, "CNPJ com dígito verificador incorreto deve ser rejeitado");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901234",
            "98765432100000",
            "11122233344455"
    })
    void deveRejeitarMultiplosCnpjsInvalidos(String cnpj) {
        assertFalse(validator.validar(cnpj), "CNPJ " + cnpj + " deveria ser inválido");
    }

    @Test
    void deveRetornarTipoDocumentoCorreto() {
        assertEquals("CNPJ", validator.getTipoDocumento());
    }

    @Test
    void deveRetornarTamanhoEsperadoCorreto() {
        assertEquals(14, validator.getTamanhoEsperado());
    }
}
