package br.com.autocenterfiap.cliente.infrastructure.validator;

import br.com.autocenterfiap.cliente.domain.service.ValidadorDocumento;

public class ValidadorCpf implements ValidadorDocumento {

    @Override
    public boolean validar(String documento) {
        if (documento == null || documento.length() != 11) {
            return false;
        }

        // Remove possíveis formatações
        documento = documento.replaceAll("\\D", "");

        if (documento.length() != 11) {
            return false;
        }

        // Verifica se todos os números são iguais
        if (documento.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int sum = 0;
        int remainder;
        for (int i = 1; i <= 9; i++) {
            sum += Integer.parseInt(documento.substring(i - 1, i)) * (11 - i);
        }
        remainder = (sum * 10) % 11;

        if (remainder == 10 || remainder == 11) {
            remainder = 0;
        }

        if (remainder != Integer.parseInt(documento.substring(9, 10))) {
            return false;
        }

        // Calcula segundo dígito verificador
        sum = 0;
        for (int i = 1; i <= 10; i++) {
            sum += Integer.parseInt(documento.substring(i - 1, i)) * (12 - i);
        }
        remainder = (sum * 10) % 11;

        if (remainder == 10 || remainder == 11) {
            remainder = 0;
        }

        return remainder == Integer.parseInt(documento.substring(10, 11));
    }

    @Override
    public String getTipoDocumento() {
        return "CPF";
    }

    @Override
    public int getTamanhoEsperado() {
        return 11;
    }
}

