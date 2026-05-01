package br.com.autocenterfiap.ordemservico.validator;

import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.springframework.stereotype.Component;

@Component
public class VeiculoValidator implements OrdemServicoValidator {
    private final VeiculoRepository veiculoRepository;

    public VeiculoValidator(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public void validate(OrdemServicoDTO dto) {
        if (!veiculoRepository.existsById(dto.veiculoId())) {
            throw new VeiculoNaoEncontradoException("Veículo com ID " + dto.veiculoId() + " não encontrado");
        }
    }


}
