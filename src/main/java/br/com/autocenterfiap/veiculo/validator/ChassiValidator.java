package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.exception.ChassiJaCadastradoException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.springframework.stereotype.Component;

@Component
public class ChassiValidator implements VeiculoValidator{
    private final VeiculoRepository veiculoRepository;

    public ChassiValidator(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public void validate(VeiculoValidationContext context) {
        String chassi = context.getVeiculoDTO().chassi();

        if (chassi == null || chassi.isBlank()) return;

        if (!chassi.matches("^[A-HJ-NPR-Z0-9]{17}$")) {
            throw new IllegalArgumentException("Chassi inválido");
        }

        if(context.isUpdate()){
            Long veiculoId = context.getVeiculoId();
            validarChassiUnicoNaAtualizacao(chassi,veiculoId);
        } else {
            validarChassiUnico(chassi);
        }

    }

    private void validarChassiUnico(String chassi){
        if(veiculoRepository.existsByChassi(chassi)){
            throw new ChassiJaCadastradoException("Já existe um Veículo com esse Chassi Cadastrado!");
        }
    }

    private void validarChassiUnicoNaAtualizacao(String chassi,Long id){
        boolean chassiJaCadastrado = veiculoRepository.existsByChassiAndIdNot(chassi,id);
        if (chassiJaCadastrado) throw new ChassiJaCadastradoException("Já existe um Veículo com esse Chassi Cadastrado!");
    }
}
