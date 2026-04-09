package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.enums.TipoOperacao;
import br.com.autocenterfiap.veiculo.exception.RenavamJaCadastradoException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.springframework.stereotype.Component;

@Component
public class RenavamValidator implements VeiculoValidator{
    private final VeiculoRepository veiculoRepository;

    public RenavamValidator(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public void validate(VeiculoValidationContext context) {
        TipoOperacao operacao = context.getOperation();
        String renavam = context.getVeiculoDTO().renavam();

        if (renavam == null || renavam.isBlank()) return;

        if (!isValidRenavam(renavam)) {
            throw new IllegalArgumentException("RENAVAM inválido");
        }

        if(operacao.equals(TipoOperacao.UPDATE)){
            Long veiculoId = context.getEntity().getId();
            validarRenavamUnicoNaAtualizacao(renavam,veiculoId);
        } else {
            validarRenavamUnico(renavam);
        }

    }

    private void validarRenavamUnico(String renavam){
        if(veiculoRepository.existsByRenavam(renavam)){
            throw new RenavamJaCadastradoException("Já existe um Veículo com esse Renavam Cadastrado!");
        }
    }

    private void validarRenavamUnicoNaAtualizacao(String renavam,Long id){
        boolean renavamJaCadastrado = veiculoRepository.existsByRenavamAndIdNot(renavam,id);
        if (renavamJaCadastrado) throw new RenavamJaCadastradoException("Já existe um Veículo com esse Renavam Cadastrado!");
    }

    private boolean isValidRenavam(String renavam) {
        renavam = renavam.replaceAll("\\D", "");

        if (renavam.length() != 11) return false;

        String base = renavam.substring(0, 10);
        int digitoInformado = Character.getNumericValue(renavam.charAt(10));

        String invertido = new StringBuilder(base).reverse().toString();

        int soma = 0;
        int peso = 2;

        for (int i = 0; i < invertido.length(); i++) {
            int num = Character.getNumericValue(invertido.charAt(i));
            soma += num * peso;
            peso++;
            if (peso > 9) peso = 2;
        }

        int resto = soma % 11;
        int digitoCalculado = (resto == 0 || resto == 1) ? 0 : 11 - resto;

        return digitoCalculado == digitoInformado;
    }
}
