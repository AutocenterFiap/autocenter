package br.com.autocenterfiap.security.adapter.in.request;

import jakarta.validation.constraints.NotBlank;

public record AlteracaoSenhaRequest(@NotBlank String nome,
                                    @NotBlank String novaSenha) {
}
