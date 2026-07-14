package br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity;

import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.domain.exception.EstoqueInsuficienteException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Table(name = "produtos")
public class ProdutoJpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String codigo;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeMedida unidadeMedida;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false)
    private Integer estoqueMinimo;

    @Column(nullable = false)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoProduto tipo;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    public void prePersist() {
        if (this.ativo == null) {
            this.ativo = true;
        }
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void decrementarEstoque(Integer quantidade) {
        if (quantidade > this.quantidadeEstoque) {
            throw new EstoqueInsuficienteException(this.codigo, this.quantidadeEstoque, quantidade);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void incrementarEstoque(Integer quantidade) {
        this.quantidadeEstoque += quantidade;
    }
}
