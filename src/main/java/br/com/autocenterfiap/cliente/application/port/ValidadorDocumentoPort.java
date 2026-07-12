package br.com.autocenterfiap.cliente.application.port;

import br.com.autocenterfiap.cliente.domain.service.ValidadorDocumento;

public interface ValidadorDocumentoPort {

    ValidadorDocumento obterValidador(String tipoCliente);
}

