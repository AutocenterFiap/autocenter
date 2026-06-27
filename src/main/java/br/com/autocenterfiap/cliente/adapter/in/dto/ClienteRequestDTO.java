package br.com.autocenterfiap.cliente.adapter.in.dto;

import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de entrada para cadastro/atualização de cliente")
public class ClienteRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    @Schema(
        description = "Nome completo do cliente ou razão social da empresa",
        example = "João da Silva",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @NotNull(message = "Tipo de cliente é obrigatório")
    @Schema(
        description = "Tipo de cliente: PESSOA_FISICA (CPF) ou PESSOA_JURIDICA (CNPJ)",
        example = "PESSOA_FISICA",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = {"PESSOA_FISICA", "PESSOA_JURIDICA"}
    )
    private TipoCliente tipoCliente;

    @NotBlank(message = "Documento é obrigatório")
    @Pattern(
        regexp = "\\d{11,14}",
        message = "Documento deve conter 11 dígitos (CPF) ou 14 dígitos (CNPJ)"
    )
    @Schema(
        description = "CPF (11 dígitos numéricos) ou CNPJ (14 dígitos numéricos)",
        example = "12345678901",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String documento;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(
        description = "Email do cliente",
        example = "joao.silva@email.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 8, max = 20, message = "Telefone deve ter entre 8 e 20 caracteres")
    @Pattern(
        regexp = "\\d{8,20}",
        message = "Telefone deve conter apenas números"
    )
    @Schema(
        description = "Telefone do cliente (apenas números, com DDD)",
        example = "11987654321",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String telefone;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    @Schema(
        description = "Endereço completo do cliente",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private EnderecoDTO endereco;

    @Schema(
        description = "Data de nascimento do cliente (para PF) ou data de fundação (para PJ)",
        example = "1990-05-15",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDate dataNascimento;
}

