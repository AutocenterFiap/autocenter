package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository.VeiculoJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class VeiculoValidator implements OrdemServicoValidator {
    private final VeiculoJpaRepository veiculoRepository;

    public VeiculoValidator(VeiculoJpaRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public void validate(CriarOrdemServicoInput dto) {
        if (!veiculoRepository.existsById(dto.veiculoId())) {
            throw new VeiculoNaoEncontradoException("Veículo com ID " + dto.veiculoId() + " não encontrado");
        }
    }


}
