package br.com.autocenterfiap.servico.mapper;

import br.com.autocenterfiap.servico.dto.ServicoDto;
import br.com.autocenterfiap.servico.dto.ServicoResponseDTO;
import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.model.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoMapper {

    public Servico toEntity(final ServicoDto dto) {
        if (dto == null) {
            return null;
        }

        Servico servico = new Servico();
        servico.setDescricao(dto.descricao());
        servico.setStatus(StatusServico.ATIVO);
        servico.setValor(dto.valor());

        return servico;
    }

    public void updateEntityFromDto(ServicoDto dto, Servico servicoExistente) {
        if (dto == null || servicoExistente == null) {
            return;
        }

        servicoExistente.setDescricao(dto.descricao());
        servicoExistente.setStatus(dto.status());
        servicoExistente.setValor(dto.valor());
    }

    public ServicoResponseDTO toServicoResponseDto(Servico servico) {
        if (servico == null) {
            return null;
        }

        return ServicoResponseDTO.builder()
                .id(servico.getId())
                .descricao(servico.getDescricao())
                .status(servico.getStatus())
                .valor(servico.getValor())
                .build();
    }
}
