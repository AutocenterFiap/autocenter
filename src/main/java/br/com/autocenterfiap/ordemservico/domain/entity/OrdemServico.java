package br.com.autocenterfiap.ordemservico.domain.entity;

import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import lombok.*;

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
public class OrdemServico implements Serializable {

    private Long id;

    private Long numeroOrdemServico;

    private StatusOS statusOS;

    private BigDecimal valorTotal;

    private Veiculo veiculo;

    private Cliente cliente;

    @Builder.Default
    private List<OSItemServico> osItensServicos = new ArrayList<>();

    @Builder.Default
    private List<OSItemProduto> osItensProdutos = new ArrayList<>();

    private LocalDateTime dataCriacao;

    private LocalDateTime dataUltimaAtualizacao;

    public void aprovar(){
        this.statusOS = StatusOS.APROVADA;
    }

    public void cancelar(){
        this.statusOS = StatusOS.CANCELADA;
    }

}
