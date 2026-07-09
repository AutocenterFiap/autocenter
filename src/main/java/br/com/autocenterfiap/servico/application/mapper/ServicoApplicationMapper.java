package br.com.autocenterfiap.servico.application.mapper;

import br.com.autocenterfiap.servico.application.dto.CriarServicoInput;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;

public class ServicoApplicationMapper {

    public static Servico toEntity(CriarServicoInput input) {
        if (input == null) return null;

        return Servico.builder()
                .descricao(input.getDescricao())
                .status(StatusServico.ATIVO) // default active on creation
                .valor(input.getValor())
                .build();
    }

    public static ServicoOutput toOutput(Servico entity) {
        if (entity == null) return null;

        return ServicoOutput.builder()
                .id(entity.getId())
                .descricao(entity.getDescricao())
                .status(entity.getStatus())
                .valor(entity.getValor())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }
}
