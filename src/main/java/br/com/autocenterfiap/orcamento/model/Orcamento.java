package br.com.autocenterfiap.orcamento.model;

import br.com.autocenterfiap.orcamento.enums.StatusOrcamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orcamento")
public class Orcamento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id")
    private Long ordemServicoId;

    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento statusOrcamento;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_ultima_atualizacao ")
    private LocalDateTime updatedAt;

    public boolean isAprovado(){
        return StatusOrcamento.APROVADO.equals(statusOrcamento);
    }

    public boolean isReprovado(){
        return StatusOrcamento.REPROVADO.equals(statusOrcamento);
    }

    public void aprovar(){
        this.statusOrcamento = StatusOrcamento.APROVADO;
    }

    public void reprovar(){
        this.statusOrcamento = StatusOrcamento.REPROVADO;
    }
}
