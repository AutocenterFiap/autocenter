package br.com.autocenterfiap.security.model;

import br.com.autocenterfiap.security.enums.PerfilType;

public record PerfilRequest(
        PerfilType nome
) {}

