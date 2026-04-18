package br.com.autocenterfiap.cliente.validator;

public class CpfValidator implements DocumentoValidator {

    private static final int TAMANHO_CPF = 11;
    private static final String TIPO = "CPF";

    @Override
    public boolean isValid(String cpf) {
        if (cpf == null || cpf.length() != TAMANHO_CPF) {
            return false;
        }

        // Verifica se todos os caracteres são dígitos
        if (!cpf.matches("\\d{" + TAMANHO_CPF + "}")) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula e valida os dígitos verificadores
        return validarDigitosVerificadores(cpf);
    }

    @Override
    public String getTipoDocumento() {
        return TIPO;
    }

    @Override
    public int getTamanhoEsperado() {
        return TAMANHO_CPF;
    }

    private boolean validarDigitosVerificadores(String cpf) {
        // Calcula o primeiro dígito verificador
        int primeiroDigito = calcularDigitoVerificador(cpf, 9, 10);

        // Verifica o primeiro dígito
        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
            return false;
        }

        // Calcula o segundo dígito verificador
        int segundoDigito = calcularDigitoVerificador(cpf, 10, 11);

        // Verifica o segundo dígito
        return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }

    private int calcularDigitoVerificador(String cpf, int limite, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < limite; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }
        int digito = 11 - (soma % 11);
        return digito >= 10 ? 0 : digito;
    }
}
