package br.com.autocenterfiap.ordemservico.adapter.mapper;

import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import jakarta.validation.Valid;

public class OrdemServicoAdapterMapper {

    public static OrdemServicoResponseDTO ordemServicoToOrdemServicoResponseDTO(OrdemServicoOutput ordemServico) {
        if (ordemServico == null) return null;

        return new OrdemServicoResponseDTO(
                ordemServico.id(),
                ordemServico.numeroOrdemServico(),
                ordemServico.statusOS(),
                ordemServico.valorTotal(),
                ordemServico.veiculoId(),
                ordemServico.clienteId()
        );
    }

    public static CriarOrdemServicoInput ordemServicoToCriarOrdemServicoInput(@Valid OrdemServicoDTO dto) {
        if (dto == null) return null;

        return new CriarOrdemServicoInput(
                dto.veiculoId(),
                dto.clienteId()
        );
    }
}
