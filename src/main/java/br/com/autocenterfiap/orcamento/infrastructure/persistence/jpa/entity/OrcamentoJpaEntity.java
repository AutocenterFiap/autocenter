package br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.entity;

import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orcamento")
@EntityListeners(AuditingEntityListener.class)
public class OrcamentoJpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServicoJpaEntity ordemServicoJpaEntity;

    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento statusOrcamento;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    public void prePersist() {
        if (this.statusOrcamento == null) {
            this.statusOrcamento = StatusOrcamento.GERADO;
        }
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}
