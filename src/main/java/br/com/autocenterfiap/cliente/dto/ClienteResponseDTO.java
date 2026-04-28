package br.com.autocenterfiap.cliente.dto;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de resposta do cliente cadastrado")
public class ClienteResponseDTO implements Serializable {

    @Schema(description = "Identificador único do cliente")
    private Long id;

    @Schema(description = "Nome completo do cliente ou razão social da empresa")
    private String nome;

    @Schema(description = "Tipo de cliente: PESSOA_FISICA (CPF) ou PESSOA_JURIDICA (CNPJ)")
    private TipoCliente tipoCliente;

    @Schema(description = "CPF (11 dígitos numéricos) ou CNPJ (14 dígitos numéricos)")
    private String documento;

    @Schema(description = "Email do cliente")
    private String email;

    @Schema(description = "Telefone do cliente (apenas números, com DDD)")
    private String telefone;

    @Schema(description = "Endereço completo do cliente")
    private EnderecoDTO endereco;

    @Schema(description = "Data de nascimento do cliente (para PF) ou data de fundação (para PJ)")
    private LocalDate dataNascimento;
}
