package br.com.autocenterfiap.cliente.validator;

public class CnpjValidator {

    public static boolean isValid(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) {
            return false;
        }

        // Verifica se todos os caracteres são dígitos
        if (!cnpj.matches("\\d{14}")) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 00.000.000/0000-00)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
        }
        int primeiroDigito = soma % 11;
        primeiroDigito = primeiroDigito < 2 ? 0 : 11 - primeiroDigito;

        // Verifica o primeiro dígito
        if (Character.getNumericValue(cnpj.charAt(12)) != primeiroDigito) {
            return false;
        }

        // Calcula o segundo dígito verificador
        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
        }
        int segundoDigito = soma % 11;
        segundoDigito = segundoDigito < 2 ? 0 : 11 - segundoDigito;

        // Verifica o segundo dígito
        return Character.getNumericValue(cnpj.charAt(13)) == segundoDigito;
    }
}
