package br.com.autocenterfiap.ordemservico.validator;

import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.exception.OrdemServicoJaAbertaParaVeiculoException;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdemServicoDuplicadaValidator implements OrdemServicoValidator{
    private final OrdemServicoRepository ordemServicoRepository;

    public OrdemServicoDuplicadaValidator(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    public void validate(OrdemServicoDTO dto) {
        List<StatusOS> statusNotAllowed = List.of(
                StatusOS.ABERTA,
                StatusOS.RECEBIDA,
                StatusOS.EM_DIAGNOSTICO,
                StatusOS.AGUARDANDO_APROVACAO,
                StatusOS.APROVADA,
                StatusOS.EM_EXECUCAO);
        if(ordemServicoRepository.existsByVeiculoIdAndStatusOSIn(dto.veiculoId(),statusNotAllowed)){
            throw new OrdemServicoJaAbertaParaVeiculoException("Já existe uma ordem de serviço em andamento para este veículo!");
        }
    }
}
