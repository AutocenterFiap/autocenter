package br.com.autocenterfiap.ordemservico.model;

import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ordem_servico")
@Schema(description = "Representa uma Ordem de Serviço da oficina automotiva")
@EntityListeners(AuditingEntityListener.class)
public class OrdemServico implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(unique = true)
    @Schema(description = "Número da Ordem de Serviço", example = "1000456", accessMode = Schema.AccessMode.READ_ONLY)
    private Long numeroOrdemServico;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_os",nullable = false, length = 20)
    @Schema(description = "Status atual da Ordem de Serviço", example = "RECEBIDA")
    private StatusOS statusOS;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Valor total da Ordem de Serviço", example = "1500.50")
    private BigDecimal valorTotal;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    @Schema(description = "Veículo vinculado à Ordem de Serviço")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @Schema(description = "Cliente vinculado à Ordem de Serviço")
    private Cliente cliente;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Lista de serviços vinculados à Ordem de Serviço")
    private List<OSItemServico> osItensServicos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Lista de produtos vinculados à Ordem de Serviço")
    private List<OSItemProduto> osItensProdutos = new ArrayList<>();

    @CreatedDate
    @Schema(description = "Data e hora da criação do registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Schema(description = "Data e hora da última atualização do registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataUltimaAtualizacao;

    public OrdemServico(OrdemServicoDTO dto,Veiculo veiculo,Cliente cliente) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.statusOS = StatusOS.ABERTA;
        this.valorTotal = BigDecimal.ZERO;
    }


}
