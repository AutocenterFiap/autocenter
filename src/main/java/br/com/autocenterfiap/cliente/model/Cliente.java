package br.com.autocenterfiap.cliente.model;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
@Schema(description = "Representa um cliente da oficina automotiva")
public class Cliente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do cliente", example = "João da Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotNull(message = "Tipo de cliente é obrigatório")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo de cliente (PESSOA_FISICA ou PESSOA_JURIDICA)", example = "PESSOA_FISICA", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoCliente tipoCliente;

    @NotBlank(message = "Documento é obrigatório")
    @Pattern(regexp = "\\d{11,14}", message = "Documento deve conter 11 dígitos (CPF) ou 14 dígitos (CNPJ)")
    @Schema(description = "CPF (11 dígitos) ou CNPJ (14 dígitos)", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED)
    private String documento;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Email do cliente", example = "joao.silva@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 8, max = 20, message = "Telefone inválido")
    @Schema(description = "Telefone do cliente", example = "11987654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefone;

    // Endereço embutido - os campos ficam na MESMA tabela 'clientes'
    @Embedded
    @NotNull(message = "Endereço é obrigatório")
    @Valid
    @Schema(description = "Endereço do cliente", requiredMode = Schema.RequiredMode.REQUIRED)
    private Endereco endereco;

    @Schema(description = "Data de nascimento do cliente", example = "1990-05-15")
    private LocalDate dataNascimento;
}
