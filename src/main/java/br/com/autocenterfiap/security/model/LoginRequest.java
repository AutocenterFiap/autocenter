package br.com.autocenterfiap.security.model;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String nome,
                           @NotBlank String senha) {
}
