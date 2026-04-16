package br.com.autocenterfiap.security.model;

import br.com.autocenterfiap.security.enums.PerfilType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PerfilRequest(
        PerfilType nome
) {}

