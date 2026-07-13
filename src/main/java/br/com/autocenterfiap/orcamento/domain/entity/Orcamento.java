package br.com.autocenterfiap.orcamento.domain.entity;

import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento implements Serializable {

    private Long id;

    private Long ordemServicoId;

    private BigDecimal valorTotal;

    private StatusOrcamento statusOrcamento;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataUltimaAtualizacao;

    public void aprovar(){
        this.statusOrcamento = StatusOrcamento.APROVADO;
    }

    public void reprovar(){
        this.statusOrcamento = StatusOrcamento.REPROVADO;
    }

    public void validarDominio() {
        if (ordemServicoId == null ) {
            throw new IllegalArgumentException("O ID da ordem de serviço não pode ser nulo");
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor total do orçamento deve ser maior que zero");
        }

        if (statusOrcamento == null) {
            throw new IllegalArgumentException("O status do orçamento deve ser informado");
        }
    }
}
