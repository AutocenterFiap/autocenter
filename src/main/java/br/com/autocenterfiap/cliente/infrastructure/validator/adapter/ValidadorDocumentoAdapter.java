package br.com.autocenterfiap.cliente.infrastructure.validator.adapter;

import br.com.autocenterfiap.cliente.application.port.ValidadorDocumentoPort;
import br.com.autocenterfiap.cliente.domain.service.ValidadorDocumento;
import br.com.autocenterfiap.cliente.infrastructure.validator.ValidadorCnpj;
import br.com.autocenterfiap.cliente.infrastructure.validator.ValidadorCpf;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDocumentoAdapter implements ValidadorDocumentoPort {

    @Override
    public ValidadorDocumento obterValidador(String tipoCliente) {
        return switch (tipoCliente) {
            case "PESSOA_FISICA" -> new ValidadorCpf();
            case "PESSOA_JURIDICA" -> new ValidadorCnpj();
            default -> throw new IllegalArgumentException("Tipo de cliente inválido: " + tipoCliente);
        };
    }
}

