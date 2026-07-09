package br.com.autocenterfiap.ordemservico.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "os_item_produto")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Representa um produto vinculado a uma Ordem de Serviço")
public class OSItemProduto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    @Schema(description = "Produto vinculado")
    private br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity produto;

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
        if (isNull(precoUnitarioNoMomento) || isNull(quantidade))
            return BigDecimal.ZERO;
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
