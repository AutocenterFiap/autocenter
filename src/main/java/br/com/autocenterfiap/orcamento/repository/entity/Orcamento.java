package br.com.autocenterfiap.orcamento.repository.entity;

import br.com.autocenterfiap.orcamento.controller.OrcamentoResponse;
import br.com.autocenterfiap.orcamento.enums.StatusOrcamento;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Orcamento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento statusOrcamento;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao ")
    private LocalDateTime dataUltimaAtualizacao;

    public boolean isAprovado(){
        return StatusOrcamento.APROVADO.equals(this.statusOrcamento);
    }

    public boolean isReprovado(){
        return StatusOrcamento.REPROVADO.equals(this.statusOrcamento);
    }

    public boolean isAguardandoAprovacao(){
        return StatusOrcamento.AGUARDANDO_APROVACAO.equals(this.statusOrcamento);
    }

    public void aprovar(){
        this.statusOrcamento = StatusOrcamento.APROVADO;
    }

    public void reprovar(){
        this.statusOrcamento = StatusOrcamento.REPROVADO;
    }

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

    public static OrcamentoResponse paraOrcamentoResponse(Orcamento orcamento) {
        return OrcamentoResponse.builder()
                .id(orcamento.getId())
                .ordemServicoId(orcamento.getOrdemServico().getId())
                .valorTotal(orcamento.getValorTotal())
                .statusOrcamento(orcamento.getStatusOrcamento())
                .dataCriacao(orcamento.getDataCriacao())
                .dataUltimaAtualizacao(orcamento.getDataUltimaAtualizacao())
                .build();
    }
}
