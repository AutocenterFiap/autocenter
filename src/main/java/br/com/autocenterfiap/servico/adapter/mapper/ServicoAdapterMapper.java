package br.com.autocenterfiap.servico.adapter.mapper;

import br.com.autocenterfiap.servico.adapter.in.dto.ServicoRequestDTO;
import br.com.autocenterfiap.servico.adapter.in.dto.ServicoResponseDTO;
import br.com.autocenterfiap.servico.application.dto.AtualizarServicoInput;
import br.com.autocenterfiap.servico.application.dto.CriarServicoInput;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;

public class ServicoAdapterMapper {

    public static CriarServicoInput criarServicoRequestToCriarServicoInput(ServicoRequestDTO request) {
        if (request == null) return null;

        return CriarServicoInput.builder()
                .descricao(request.getDescricao())
                .status(request.getStatus())
                .valor(request.getValor())
                .build();
    }

    public static AtualizarServicoInput atualizarServicoRequestToAtualizarServicoInput(ServicoRequestDTO request) {
        if (request == null) return null;

        return AtualizarServicoInput.builder()
                .descricao(request.getDescricao())
                .status(request.getStatus())
                .valor(request.getValor())
                .build();
    }

    public static ServicoResponseDTO servicoOutputToServicoResponse(ServicoOutput output) {
        if (output == null) return null;

        return ServicoResponseDTO.builder()
                .id(output.getId())
                .descricao(output.getDescricao())
                .status(output.getStatus())
                .valor(output.getValor())
                .build();
    }
}
