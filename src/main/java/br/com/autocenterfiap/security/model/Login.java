package br.com.autocenterfiap.security.model;

import jakarta.validation.constraints.NotBlank;

public record Login(@NotBlank String clientId,
                    @NotBlank String clientSecret) {
}
