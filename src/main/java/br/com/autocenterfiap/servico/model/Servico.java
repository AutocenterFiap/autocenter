package br.com.autocenterfiap.servico.model;

import br.com.autocenterfiap.servico.enums.StatusServico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "servicos")
@Schema(description = "Representa um servico da oficina automotiva")
@EntityListeners(AuditingEntityListener.class)
public class Servico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do servico", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Tipo é obrigatório")
    @Schema(description = "Tipo do serviço", example = "Troca pastilhas de freio", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descricao;

    @NotBlank(message = "Status é obrigatório")
    @Schema(description = "Status do serviço", example = "ATIVO", requiredMode = Schema.RequiredMode.REQUIRED)
    private StatusServico status;

    @NotNull(message = "Valor é obrigatório")
    @Schema(description = "Valor do serviço", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal valor;

    @CreatedDate
    @Column(updatable = false, name = "data_criacao")
    @Schema(description = "Data e hora da criação do serviço", example = "2023-10-01T10:00:00")
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(updatable = false, name = "data_ultima_atualizacao")
    @Schema(description = "Data e hora da última atualização do serviço", example = "2023-10-01T12:00:00")
    private LocalDateTime dataUltimaAtualizacao;

}
