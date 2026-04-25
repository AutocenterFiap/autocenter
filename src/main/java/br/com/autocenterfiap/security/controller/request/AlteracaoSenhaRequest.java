package br.com.autocenterfiap.security.controller.request;

import jakarta.validation.constraints.NotBlank;

public record AlteracaoSenhaRequest(@NotBlank String nome,
                                    @NotBlank String novaSenha) {
}
