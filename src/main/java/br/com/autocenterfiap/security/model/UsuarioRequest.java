package br.com.autocenterfiap.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UsuarioRequest(@NotBlank String nome,
                             @NotBlank String senha,
                             @NotEmpty(message = "A lista de perfis não pode estar vazia")
                             @Valid
                             @JsonProperty("perfis")  List<PerfilRequest> perfis) {
}
