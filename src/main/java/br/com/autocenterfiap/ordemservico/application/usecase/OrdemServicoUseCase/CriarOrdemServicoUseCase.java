package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.application.validator.OrdemServicoValidator;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CriarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final List<OrdemServicoValidator> validators;

    public CriarOrdemServicoUseCase(OrdemServicoRepositoryPort ordemServicoRepositoryPort, VeiculoRepositoryPort veiculoRepositoryPort, ClienteRepositoryPort clienteRepositoryPort, List<OrdemServicoValidator> validators) {
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
        this.veiculoRepositoryPort = veiculoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.validators = validators;
    }

    @Transactional
    public OrdemServicoOutput executar(CriarOrdemServicoInput input) {
        validators.forEach(validator -> validator.validate(input));

        Veiculo veiculo = this.veiculoRepositoryPort.buscarPorId(input.veiculoId())
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo Não encontrado para o id: "
                        + input.veiculoId()));

        Cliente cliente = this.clienteRepositoryPort.buscarPorId(input.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não enontrado para o id: "
                        + input.clienteId()));

        OrdemServico ordemServico = OrdemServicoApplicationMapper.toEntity(input, veiculo, cliente);

        OrdemServico ordemServicoSalvo = this.ordemServicoRepositoryPort.save(ordemServico);

        return OrdemServicoApplicationMapper.toOutput(ordemServicoSalvo);
    }
}
