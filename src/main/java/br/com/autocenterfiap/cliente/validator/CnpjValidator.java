package br.com.autocenterfiap.cliente.validator;

public class CnpjValidator implements DocumentoValidator {

    private static final int TAMANHO_CNPJ = 14;
    private static final String TIPO = "CNPJ";
    private static final int[] PESOS_PRIMEIRO_DIGITO = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_SEGUNDO_DIGITO = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public boolean isValid(String cnpj) {
        if (cnpj == null || cnpj.length() != TAMANHO_CNPJ) {
            return false;
        }

        // Verifica se todos os caracteres são dígitos
        if (!cnpj.matches("\\d{" + TAMANHO_CNPJ + "}")) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 00.000.000/0000-00)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Calcula e valida os dígitos verificadores
        return validarDigitosVerificadores(cnpj);
    }

    @Override
    public String getTipoDocumento() {
        return TIPO;
    }

    @Override
    public int getTamanhoEsperado() {
        return TAMANHO_CNPJ;
    }

    private boolean validarDigitosVerificadores(String cnpj) {
        // Calcula o primeiro dígito verificador
        int primeiroDigito = calcularDigitoVerificador(cnpj, PESOS_PRIMEIRO_DIGITO, 12);

        // Verifica o primeiro dígito
        if (Character.getNumericValue(cnpj.charAt(12)) != primeiroDigito) {
            return false;
        }

        // Calcula o segundo dígito verificador
        int segundoDigito = calcularDigitoVerificador(cnpj, PESOS_SEGUNDO_DIGITO, 13);

        // Verifica o segundo dígito
        return Character.getNumericValue(cnpj.charAt(13)) == segundoDigito;
    }

    private int calcularDigitoVerificador(String cnpj, int[] pesos, int limite) {
        int soma = 0;
        for (int i = 0; i < limite; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
