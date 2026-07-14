package br.com.autocenterfiap.security.adapter.in.request;

import br.com.autocenterfiap.security.domain.enums.PerfilType;

public record PerfilRequest(
        PerfilType nome
) {}

