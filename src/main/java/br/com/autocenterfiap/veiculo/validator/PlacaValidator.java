package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.exception.PlacaJaCadastradaException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.springframework.stereotype.Component;


@Component
public class PlacaValidator implements VeiculoValidator{
    private final VeiculoRepository veiculoRepository;

    public PlacaValidator(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public void validate(VeiculoValidationContext context) {
        VeiculoDTO veiculoDTO = context.getVeiculoDTO();

        if(context.isUpdate()){
            Long veiculoId = context.getVeiculoId();
            validarPlacaUnicaNaAtualizacao(veiculoDTO.placa(), veiculoId);
        } else {
            validarPlacaUnica(veiculoDTO.placa());
        }
    }

    private void validarPlacaUnica(String placa){
        if(veiculoRepository.existsByPlaca(placa)){
            throw new PlacaJaCadastradaException("Já existe um Veículo com essa Placa Cadastrada!");
        }
    }

    private void validarPlacaUnicaNaAtualizacao(String placa,Long id){
        boolean placaJaCadastrada = veiculoRepository.existsByPlacaAndIdNot(placa,id);
        if (placaJaCadastrada) throw new PlacaJaCadastradaException("Já existe um Veículo com essa Placa Cadastrada!");
    }
}
