package br.com.autocenterfiap.produto.domain.entity;

import br.com.autocenterfiap.produto.domain.enums.StatusEstoque;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.domain.exception.EstoqueInsuficienteException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private UnidadeMedida unidadeMedida;
    private BigDecimal precoUnitario;
    private Integer quantidadeEstoque;
    private Integer estoqueMinimo;
    private String categoria;
    private TipoProduto tipo;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;

    public void validarDominio() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do produto é obrigatório");
        }
        if (unidadeMedida == null) {
            throw new IllegalArgumentException("Unidade de medida é obrigatória");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço unitário é obrigatório e não pode ser negativo");
        }
        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque é obrigatória e não pode ser negativa");
        }
        if (estoqueMinimo == null || estoqueMinimo < 0) {
            throw new IllegalArgumentException("Estoque mínimo é obrigatório e não pode ser negativo");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("Categoria do produto é obrigatória");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo do produto é obrigatório");
        }
    }

    public void atualizarDados(String nome, String codigo, String descricao, UnidadeMedida unidadeMedida,
                               BigDecimal precoUnitario, Integer quantidadeEstoque, Integer estoqueMinimo,
                               String categoria, TipoProduto tipo) {
        this.nome = nome;
        this.codigo = codigo;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.precoUnitario = precoUnitario;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
        this.categoria = categoria;
        this.tipo = tipo;
        this.dataUltimaAtualizacao = LocalDateTime.now();
        validarDominio();
    }

    public void decrementarEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para decrementar deve ser maior que zero");
        }
        if (quantidade > this.quantidadeEstoque) {
            throw new EstoqueInsuficienteException(this.codigo, this.quantidadeEstoque, quantidade);
        }
        this.quantidadeEstoque -= quantidade;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void incrementarEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para incrementar deve ser maior que zero");
        }
        this.quantidadeEstoque += quantidade;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public StatusEstoque getStatusEstoque() {
        if (this.quantidadeEstoque == 0) {
            return StatusEstoque.OUT_OF_STOCK;
        }
        if (this.quantidadeEstoque < this.estoqueMinimo) {
            return StatusEstoque.LOW_STOCK;
        }
        return StatusEstoque.NORMAL;
    }

    public boolean isEstoqueBaixo() {
        return this.quantidadeEstoque < this.estoqueMinimo;
    }

    public void desativar() {
        this.ativo = false;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}
