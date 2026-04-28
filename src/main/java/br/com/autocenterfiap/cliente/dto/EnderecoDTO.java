package br.com.autocenterfiap.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de endereço do cliente")
public class EnderecoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(
        regexp = "\\d{8}",
        message = "CEP deve conter exatamente 8 dígitos numéricos"
    )
    @Schema(
        description = "CEP (8 dígitos numéricos, sem hífen)",
        example = "01310100",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(min = 3, max = 255, message = "Logradouro deve ter entre 3 e 255 caracteres")
    @Schema(
        description = "Nome da rua/avenida",
        example = "Avenida Paulista",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String logradouro;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Schema(
        description = "Número do imóvel",
        example = "1578",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String numero;

    @Size(max = 255, message = "Complemento deve ter no máximo 255 caracteres")
    @Schema(
        description = "Complemento do endereço",
        example = "Andar 5, Sala 502",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String complemento;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Schema(
        description = "Bairro",
        example = "Bela Vista",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 2, max = 100, message = "Cidade deve ter entre 2 e 100 caracteres")
    @Schema(
        description = "Nome da cidade",
        example = "São Paulo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Pattern(
        regexp = "[A-Z]{2}",
        message = "Estado deve ser a sigla UF com 2 letras maiúsculas"
    )
    @Schema(
        description = "Sigla do estado (UF - 2 letras maiúsculas)",
        example = "SP",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;
}
