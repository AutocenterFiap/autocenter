package br.com.autocenterfiap.cliente.infrastructure.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CPF Validator - Testes Unitários")
class ValidadorCpfTest {

    private ValidadorCpf validator;

    @BeforeEach
    void setUp() {
        validator = new ValidadorCpf();
    }

    @Test
    void deveValidarCpfValido() {
        String cpfValido = "11144477735"; // CPF válido de teste
        boolean resultado = validator.validar(cpfValido);
        assertTrue(resultado, "CPF válido deve ser aceito");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678909",
            "11144477735",
            "52998224725"
    })
    void deveValidarMultiplosCpfsValidos(String cpf) {
        assertTrue(validator.validar(cpf), "CPF " + cpf + " deveria ser válido");
    }

    @Test
    void deveRejeitarCpfComTodosDigitosIguais() {
        assertFalse(validator.validar("11111111111"));
        assertFalse(validator.validar("00000000000"));
        assertFalse(validator.validar("99999999999"));
    }

    @Test
    void deveRejeitarCpfNulo() {
        assertFalse(validator.validar(null));
    }

    @Test
    void deveRejeitarCpfComTamanhoIncorreto() {
        assertFalse(validator.validar("123456789"));     // muito curto
        assertFalse(validator.validar("123456789012"));  // muito longo
        assertFalse(validator.validar(""));              // vazio
    }

    @Test
    void deveRejeitarCpfComCaracteresNaoNumericos() {
        assertFalse(validator.validar("111.444.777-35")); // com formatação
        assertFalse(validator.validar("111444777ab"));    // com letras
        assertFalse(validator.validar("111 444 777 35")); // com espaços
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorIncorreto() {
        String cpfInvalido = "11144477700"; // dígito verificador errado
        boolean resultado = validator.validar(cpfInvalido);
        assertFalse(resultado, "CPF com dígito verificador incorreto deve ser rejeitado");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",
            "11122233344"
    })
    void deveRejeitarMultiplosCpfsInvalidos(String cpf) {
        assertFalse(validator.validar(cpf), "CPF " + cpf + " deveria ser inválido");
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
