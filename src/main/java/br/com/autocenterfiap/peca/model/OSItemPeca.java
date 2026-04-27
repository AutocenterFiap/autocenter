package br.com.autocenterfiap.peca.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "os_item_peca")
@Schema(description = "Representa uma peça vinculada a uma Ordem de Serviço")
public class OSItemPeca implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    @Schema(description = "ID da Ordem de Serviço", example = "1")
    private Long ordemServicoId;

    @ManyToOne
    @JoinColumn(name = "peca_id", nullable = false)
    @Schema(description = "Peça vinculada")
    private Peca peca;

    @Column(nullable = false)
    @Schema(description = "Quantidade utilizada", example = "2")
    private Integer quantidade;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Preço unitário no momento da inclusão (snapshot)", example = "45.90")
    private BigDecimal precoUnitarioNoMomento;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataUltimaAtualizacao;

    // ── Regra de domínio ──────────────────────────────────────────────────────

    public BigDecimal calcularSubtotal() {
        return this.precoUnitarioNoMomento.multiply(BigDecimal.valueOf(this.quantidade));
    }

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}
