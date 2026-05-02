package br.com.autocenterfiap.security.controller.request;

import br.com.autocenterfiap.security.serializador.UpperCaseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank
                           @JsonDeserialize(using = UpperCaseDeserializer.class)
                           String nome,
                           @NotBlank String senha) {
}
