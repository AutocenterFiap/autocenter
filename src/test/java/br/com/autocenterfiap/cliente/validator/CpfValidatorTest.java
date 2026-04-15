package br.com.autocenterfiap.cliente.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CPF Validator - Testes Unitários")
class CpfValidatorTest {

    @Test
    void deveValidarCpfValido() {
        String cpfValido = "11144477735"; // CPF válido de teste
        boolean resultado = CpfValidator.isValid(cpfValido);
        assertTrue(resultado, "CPF válido deve ser aceito");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678909",
            "11144477735",
            "52998224725"
    })
    void deveValidarMultiplosCpfsValidos(String cpf) {
        assertTrue(CpfValidator.isValid(cpf), "CPF " + cpf + " deveria ser válido");
    }

    @Test
    void deveRejeitarCpfComTodosDigitosIguais() {
        assertFalse(CpfValidator.isValid("11111111111"));
        assertFalse(CpfValidator.isValid("00000000000"));
        assertFalse(CpfValidator.isValid("99999999999"));
    }

    @Test
    void deveRejeitarCpfNulo() {
        assertFalse(CpfValidator.isValid(null));
    }

    @Test
    void deveRejeitarCpfComTamanhoIncorreto() {
        assertFalse(CpfValidator.isValid("123456789"));     // muito curto
        assertFalse(CpfValidator.isValid("123456789012"));  // muito longo
        assertFalse(CpfValidator.isValid(""));              // vazio
    }

    @Test
    void deveRejeitarCpfComCaracteresNaoNumericos() {
        assertFalse(CpfValidator.isValid("111.444.777-35")); // com formatação
        assertFalse(CpfValidator.isValid("111444777ab"));    // com letras
        assertFalse(CpfValidator.isValid("111 444 777 35")); // com espaços
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorIncorreto() {
        String cpfInvalido = "11144477700"; // dígito verificador errado
        boolean resultado = CpfValidator.isValid(cpfInvalido);
        assertFalse(resultado, "CPF com dígito verificador incorreto deve ser rejeitado");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",
            "11122233344"
    })
    void deveRejeitarMultiplosCpfsInvalidos(String cpf) {
        assertFalse(CpfValidator.isValid(cpf), "CPF " + cpf + " deveria ser inválido");
    }
}
