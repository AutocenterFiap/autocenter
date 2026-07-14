package br.com.autocenterfiap.cliente.infrastructure.validator;

import br.com.autocenterfiap.cliente.domain.service.ValidadorDocumento;

public class ValidadorCnpj implements ValidadorDocumento {

    @Override
    public boolean validar(String documento) {
        if (documento == null || documento.length() != 14) {
            return false;
        }

        // Remove possíveis formatações
        documento = documento.replaceAll("\\D", "");

        if (documento.length() != 14) {
            return false;
        }

        // Verifica se todos os números são iguais
        if (documento.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int sum = 0;
        int multiplier = 5;
        for (int i = 0; i < 12; i++) {
            sum += Integer.parseInt(documento.substring(i, i + 1)) * multiplier;
            multiplier--;
            if (multiplier < 2) {
                multiplier = 9;
            }
        }

        int remainder = sum % 11;
        int firstVerifier = remainder < 2 ? 0 : 11 - remainder;

        if (firstVerifier != Integer.parseInt(documento.substring(12, 13))) {
            return false;
        }

        // Calcula segundo dígito verificador
        sum = 0;
        multiplier = 6;
        for (int i = 0; i < 13; i++) {
            sum += Integer.parseInt(documento.substring(i, i + 1)) * multiplier;
            multiplier--;
            if (multiplier < 2) {
                multiplier = 9;
            }
        }

        remainder = sum % 11;
        int secondVerifier = remainder < 2 ? 0 : 11 - remainder;

        return secondVerifier == Integer.parseInt(documento.substring(13, 14));
    }

    @Override
    public String getTipoDocumento() {
        return "CNPJ";
    }

    @Override
    public int getTamanhoEsperado() {
        return 14;
    }
}

