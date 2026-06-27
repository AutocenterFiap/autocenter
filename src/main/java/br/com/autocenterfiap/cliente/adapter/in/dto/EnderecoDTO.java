package br.com.autocenterfiap.cliente.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de endereço do cliente")
public class EnderecoDTO {

    @NotBlank(message = "CEP é obrigatório")
    @Size(min = 8, max = 8, message = "CEP deve ter 8 dígitos")
    @Schema(description = "CEP do endereço", example = "01310100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Logradouro (rua, avenida, etc)", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String logradouro;

    @Schema(description = "Número do endereço", example = "100", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String numero;

    @Schema(description = "Complemento do endereço", example = "Aptto 1000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String complemento;

    @Schema(description = "Bairro", example = "Bela Vista", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Schema(description = "Estado (UF)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String estado;
}

