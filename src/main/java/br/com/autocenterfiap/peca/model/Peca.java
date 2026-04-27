package br.com.autocenterfiap.peca.model;

import br.com.autocenterfiap.peca.dto.PecaRequestDTO;
import br.com.autocenterfiap.peca.enums.StatusEstoque;
import br.com.autocenterfiap.peca.enums.TipoPeca;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.exception.EstoqueInsuficienteException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "pecas")
@Schema(description = "Representa uma Peça ou Insumo do estoque da oficina")
public class Peca implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da peça", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome da peça", example = "Filtro de Óleo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Column(nullable = false, unique = true)
    @Schema(description = "Código interno único da peça", example = "FO-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codigo;

    @Schema(description = "Descrição da peça", example = "Filtro de óleo para motores 1.0 a 2.0")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Schema(description = "Unidade de medida", example = "UNIT", requiredMode = Schema.RequiredMode.REQUIRED)
    private UnidadeMedida unidadeMedida;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Preço unitário da peça", example = "45.90", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    @Schema(description = "Quantidade disponível em estoque", example = "100")
    private Integer quantidadeEstoque;

    @Column(nullable = false)
    @Schema(description = "Quantidade mínima de estoque (alerta de estoque baixo)", example = "10")
    private Integer estoqueMinimo;

    @Column(nullable = false)
    @Schema(description = "Categoria da peça", example = "Motor")
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Schema(description = "Tipo do item: PECAS ou INSUMOS", example = "PECAS", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoPeca tipo;

    @Column(nullable = false)
    @Schema(description = "Indica se a peça está ativa", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean ativo;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataUltimaAtualizacao;

    public Peca(PecaRequestDTO dto) {
        aplicarDados(dto);
        this.ativo = true;
    }

    public void atualizarDados(PecaRequestDTO dto) {
        aplicarDados(dto);
    }

    private void aplicarDados(PecaRequestDTO dto) {
        this.nome = dto.nome();
        this.codigo = dto.codigo();
        this.descricao = dto.descricao();
        this.unidadeMedida = dto.unidadeMedida();
        this.precoUnitario = dto.precoUnitario();
        this.quantidadeEstoque = dto.quantidadeEstoque();
        this.estoqueMinimo = dto.estoqueMinimo();
        this.categoria = dto.categoria();
        this.tipo = dto.tipo();
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
    }

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
}
