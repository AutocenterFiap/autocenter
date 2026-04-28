package br.com.autocenterfiap.cliente.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CNPJ Validator - Testes Unitários")
class CnpjValidatorTest {

    private CnpjValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CnpjValidator();
    }

    @Test
    void deveValidarCnpjValido() {
        String cnpjValido = "11222333000181"; // CNPJ válido de teste
        boolean resultado = validator.isValid(cnpjValido);
        assertTrue(resultado, "CNPJ válido deve ser aceito");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11222333000181",
            "34028316000103",
            "60701190000104"
    })
    void deveValidarMultiplosCnpjsValidos(String cnpj) {
        assertTrue(validator.isValid(cnpj), "CNPJ " + cnpj + " deveria ser válido");
    }

    @Test
    void deveRejeitarCnpjComTodosDigitosIguais() {
        assertFalse(validator.isValid("11111111111111"));
        assertFalse(validator.isValid("00000000000000"));
        assertFalse(validator.isValid("99999999999999"));
    }

    @Test
    void deveRejeitarCnpjNulo() {
        assertFalse(validator.isValid(null));
    }

    @Test
    void deveRejeitarCnpjComTamanhoIncorreto() {
        assertFalse(validator.isValid("1122233300018"));   // muito curto
        assertFalse(validator.isValid("112223330001811")); // muito longo
        assertFalse(validator.isValid(""));                // vazio
    }

    @Test
    void deveRejeitarCnpjComCaracteresNaoNumericos() {
        assertFalse(validator.isValid("11.222.333/0001-81")); // com formatação
        assertFalse(validator.isValid("1122233300018X"));     // com letras
        assertFalse(validator.isValid("11 222 333 0001 81")); // com espaços
    }

    @Test
    void deveRejeitarCnpjComDigitoVerificadorIncorreto() {
        String cnpjInvalido = "11222333000100"; // dígito verificador errado
        boolean resultado = validator.isValid(cnpjInvalido);
        assertFalse(resultado, "CNPJ com dígito verificador incorreto deve ser rejeitado");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901234",
            "98765432100000",
            "11122233344455"
    })
    void deveRejeitarMultiplosCnpjsInvalidos(String cnpj) {
        assertFalse(validator.isValid(cnpj), "CNPJ " + cnpj + " deveria ser inválido");
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
