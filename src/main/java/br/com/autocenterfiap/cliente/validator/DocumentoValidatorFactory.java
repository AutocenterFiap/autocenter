package br.com.autocenterfiap.cliente.validator;

import br.com.autocenterfiap.cliente.enums.TipoCliente;

public class DocumentoValidatorFactory {

    private static final CpfValidator CPF_VALIDATOR = new CpfValidator();
    private static final CnpjValidator CNPJ_VALIDATOR = new CnpjValidator();

    public static DocumentoValidator getValidator(TipoCliente tipoCliente) {
        if (tipoCliente == null) {
            throw new IllegalArgumentException("Tipo de cliente não pode ser nulo");
        }

        return switch (tipoCliente) {
            case PESSOA_FISICA -> CPF_VALIDATOR;
            case PESSOA_JURIDICA -> CNPJ_VALIDATOR;
        };
    }

    public static DocumentoValidator getValidatorByDocumento(String documento) {
        if (documento == null) {
            throw new IllegalArgumentException("Documento não pode ser nulo");
        }

        int tamanho = documento.length();

        if (tamanho == 11) {
            return CPF_VALIDATOR;
        } else if (tamanho == 14) {
            return CNPJ_VALIDATOR;
        } else {
            throw new IllegalArgumentException(
                "Tamanho de documento inválido: " + tamanho +
                ". Esperado: 11 (CPF) ou 14 (CNPJ)"
            );
        }
    }

    private DocumentoValidatorFactory() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }
}
