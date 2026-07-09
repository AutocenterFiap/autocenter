package br.com.autocenterfiap.veiculo.application.usecase;

import java.time.LocalDateTime;

import br.com.autocenterfiap.veiculo.application.dto.CriarVeiculoInput;
import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.mapper.VeiculoApplicationMapper;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.*;

public class CriarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    public CriarVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        this.veiculoRepositoryPort = veiculoRepositoryPort;
    }

    public VeiculoOutput executar(CriarVeiculoInput input) {
        Veiculo veiculo = VeiculoApplicationMapper.toEntity(input);
        veiculo.validarDominio();

        // Validar formato do Chassi
        String chassi = veiculo.getChassi();
        if (chassi != null && !chassi.isBlank()) {
            if (!chassi.matches("^[A-HJ-NPR-Z0-9]{17}$")) {
                throw new ChassiInvalidoException("Chassi inválido");
            }
            // Validar unicidade do Chassi
            if (veiculoRepositoryPort.existePorChassi(chassi)) {
                throw new ChassiJaCadastradoException("Já existe um Veículo com esse Chassi Cadastrado!");
            }
        }

        // Validar formato do Renavam
        String renavam = veiculo.getRenavam();
        if (renavam != null && !renavam.isBlank()) {
            if (!isValidRenavam(renavam)) {
                throw new RenavamInvalidoException("RENAVAM inválido");
            }
            // Validar unicidade do Renavam
            if (veiculoRepositoryPort.existePorRenavam(renavam)) {
                throw new RenavamJaCadastradoException("Já existe um Veículo com esse Renavam Cadastrado!");
            }
        }

        // Validar unicidade da Placa
        String placa = veiculo.getPlaca();
        if (veiculoRepositoryPort.existePorPlaca(placa)) {
            throw new PlacaJaCadastradaException("Já existe um Veículo com essa Placa Cadastrada!");
        }

        veiculo.setDataCadastro(LocalDateTime.now());
        veiculo.setDataUltimaAtualizacao(LocalDateTime.now());

        Veiculo veiculoSalvo = veiculoRepositoryPort.salvar(veiculo);
        return VeiculoApplicationMapper.toOutput(veiculoSalvo);
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
