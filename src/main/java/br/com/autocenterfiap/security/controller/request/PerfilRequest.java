package br.com.autocenterfiap.security.controller.request;

import br.com.autocenterfiap.security.enums.PerfilType;

public record PerfilRequest(
        PerfilType nome
) {}

