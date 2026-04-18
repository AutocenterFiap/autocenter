package br.com.autocenterfiap.cliente.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Schema(description = "Endereço completo do cliente")
public class Endereco implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos numéricos")
    @Schema(description = "CEP (8 dígitos numéricos)", example = "01310100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Nome da rua/avenida", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String logradouro;

    @Schema(description = "Número do imóvel", example = "1578")
    private String numero;

    @Schema(description = "Complemento do endereço", example = "Andar 5")
    private String complemento;

    @Schema(description = "Bairro", example = "Bela Vista")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Schema(description = "Estado (UF - 2 letras)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String estado;
}
