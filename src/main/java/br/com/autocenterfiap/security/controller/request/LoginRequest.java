package br.com.autocenterfiap.security.controller.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String nome,
                           @NotBlank String senha) {
}
