package br.com.autocenterfiap.cliente.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CPF Validator - Testes Unitários")
class CpfValidatorTest {

    private CpfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
    }

    @Test
    void deveValidarCpfValido() {
        String cpfValido = "11144477735"; // CPF válido de teste
        boolean resultado = validator.isValid(cpfValido);
        assertTrue(resultado, "CPF válido deve ser aceito");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678909",
            "11144477735",
            "52998224725"
    })
    void deveValidarMultiplosCpfsValidos(String cpf) {
        assertTrue(validator.isValid(cpf), "CPF " + cpf + " deveria ser válido");
    }

    @Test
    void deveRejeitarCpfComTodosDigitosIguais() {
        assertFalse(validator.isValid("11111111111"));
        assertFalse(validator.isValid("00000000000"));
        assertFalse(validator.isValid("99999999999"));
    }

    @Test
    void deveRejeitarCpfNulo() {
        assertFalse(validator.isValid(null));
    }

    @Test
    void deveRejeitarCpfComTamanhoIncorreto() {
        assertFalse(validator.isValid("123456789"));     // muito curto
        assertFalse(validator.isValid("123456789012"));  // muito longo
        assertFalse(validator.isValid(""));              // vazio
    }

    @Test
    void deveRejeitarCpfComCaracteresNaoNumericos() {
        assertFalse(validator.isValid("111.444.777-35")); // com formatação
        assertFalse(validator.isValid("111444777ab"));    // com letras
        assertFalse(validator.isValid("111 444 777 35")); // com espaços
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorIncorreto() {
        String cpfInvalido = "11144477700"; // dígito verificador errado
        boolean resultado = validator.isValid(cpfInvalido);
        assertFalse(resultado, "CPF com dígito verificador incorreto deve ser rejeitado");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",
            "11122233344"
    })
    void deveRejeitarMultiplosCpfsInvalidos(String cpf) {
        assertFalse(validator.isValid(cpf), "CPF " + cpf + " deveria ser inválido");
    }

    @Test
    void deveRetornarTipoDocumentoCorreto() {
        assertEquals("CPF", validator.getTipoDocumento());
    }

    @Test
    void deveRetornarTamanhoEsperadoCorreto() {
        assertEquals(11, validator.getTamanhoEsperado());
    }
}
