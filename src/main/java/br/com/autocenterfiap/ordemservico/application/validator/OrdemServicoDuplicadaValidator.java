package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoJaAbertaParaVeiculoException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdemServicoDuplicadaValidator implements OrdemServicoValidator{
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public OrdemServicoDuplicadaValidator(OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    @Override
    public void validate(CriarOrdemServicoInput dto) {
        List<StatusOS> statusNotAllowed = List.of(
                StatusOS.ABERTA,
                StatusOS.RECEBIDA,
                StatusOS.EM_DIAGNOSTICO,
                StatusOS.AGUARDANDO_APROVACAO,
                StatusOS.APROVADA,
                StatusOS.EM_EXECUCAO);
        if(this.ordemServicoRepositoryPort.existsByVeiculoIdAndStatusOSIn(dto.veiculoId(),statusNotAllowed)){
            throw new OrdemServicoJaAbertaParaVeiculoException("Já existe uma ordem de serviço em andamento para este veículo!");
        }
    }
}
