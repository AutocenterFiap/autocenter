package br.com.autocenterfiap.produto.application.dto;

import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtualizarProdutoInput {
    private String nome;
    private String codigo;
    private String descricao;
    private UnidadeMedida unidadeMedida;
    private BigDecimal precoUnitario;
    private Integer quantidadeEstoque;
    private Integer estoqueMinimo;
    private String categoria;
    private TipoProduto tipo;
}
