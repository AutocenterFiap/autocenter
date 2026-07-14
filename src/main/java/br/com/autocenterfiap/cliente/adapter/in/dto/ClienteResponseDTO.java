package br.com.autocenterfiap.cliente.adapter.in.dto;

import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de resposta do cliente")
public class ClienteResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(
        description = "ID do cliente",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Nome completo do cliente",
        example = "João da Silva"
    )
    private String nome;

    @Schema(
        description = "Tipo de cliente",
        example = "PESSOA_FISICA"
    )
    private TipoCliente tipoCliente;

    @Schema(
        description = "Documento valido do cliente",
        example = "12345678901",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String documento;

    @Schema(
        description = "Email do cliente",
        example = "joao.silva@email.com"
   )
    private String email;

    @Schema(
        description = "Telefone do cliente",
        example = "11987654321"
    )
    private String telefone;

    @Schema(
        description = "Endereço do cliente"
    )
    private EnderecoDTO endereco;

    @Schema(
        description = "Data de nascimento",
        example = "1990-05-15"
    )
    private LocalDate dataNascimento;

    @Schema(
        description = "Data de criação do cliente",
        example = "2024-01-10T14:30:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime dataCriacao;

    @Schema(
        description = "Data da última atualização",
        example = "2024-01-15T10:20:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime dataUltimaAtualizacao;
}

