package br.com.autocenterfiap.ordemservico.application.mapper;

import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;

import java.math.BigDecimal;

public class OrdemServicoApplicationMapper {

    public static OrdemServico toEntity(CriarOrdemServicoInput dto, Veiculo veiculo, Cliente cliente) {
        if (dto == null) return null;

        return OrdemServico.builder()
                .statusOS(StatusOS.ABERTA)
                .valorTotal(BigDecimal.ZERO)
                .veiculo(veiculo)
                .cliente(cliente)
                .build();
    }

    public static OrdemServicoOutput toOutput(OrdemServico entity) {
        if (entity == null) return null;

        return new OrdemServicoOutput(
                entity.getId(),
                entity.getNumeroOrdemServico(),
                entity.getStatusOS(),
                entity.getValorTotal(),
                entity.getVeiculo().getId(),
                entity.getCliente().getId()
        );
    }
}
