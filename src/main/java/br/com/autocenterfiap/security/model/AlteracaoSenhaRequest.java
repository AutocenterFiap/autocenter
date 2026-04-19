package br.com.autocenterfiap.security.model;

import jakarta.validation.constraints.NotBlank;

public record AlteracaoSenhaRequest(@NotBlank String nome,
                                    @NotBlank String novaSenha) {
}
