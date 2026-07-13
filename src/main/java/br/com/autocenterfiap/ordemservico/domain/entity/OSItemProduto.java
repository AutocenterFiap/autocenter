package br.com.autocenterfiap.ordemservico.domain.entity;

import br.com.autocenterfiap.produto.domain.entity.Produto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OSItemProduto implements Serializable {

    private Long id;

    private OrdemServico ordemServico;

    private Produto produto;

    private Integer quantidade;

    private BigDecimal precoUnitarioNoMomento;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataUltimaAtualizacao;

    // ── Regra de domínio ──────────────────────────────────────────────────────

    public BigDecimal calcularSubtotal() {
        if (isNull(precoUnitarioNoMomento) || isNull(quantidade))
            return BigDecimal.ZERO;
        return this.precoUnitarioNoMomento.multiply(BigDecimal.valueOf(this.quantidade));
    }
}
