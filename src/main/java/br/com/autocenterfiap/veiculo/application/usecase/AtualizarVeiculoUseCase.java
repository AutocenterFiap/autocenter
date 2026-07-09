package br.com.autocenterfiap.veiculo.application.usecase;

import java.time.LocalDateTime;

import br.com.autocenterfiap.veiculo.application.dto.AtualizarVeiculoInput;
import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.mapper.VeiculoApplicationMapper;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.*;

public class AtualizarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    public AtualizarVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        this.veiculoRepositoryPort = veiculoRepositoryPort;
    }

    public VeiculoOutput executar(Long id, AtualizarVeiculoInput input) {
        Veiculo veiculo = veiculoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new VeiculoNaoEncontradoException("Veiculo com ID " + id + " não existe!"));

        // Validar unicidade da Placa se mudou
        String placa = input.getPlaca();
        if (veiculoRepositoryPort.existePorPlacaEIdDiferente(placa, id)) {
            throw new PlacaJaCadastradaException("Já existe um Veículo com essa Placa Cadastrada!");
        }

        // Validar formato e unicidade do Chassi se mudou
        String chassi = input.getChassi();
        if (chassi != null && !chassi.isBlank()) {
            if (!chassi.matches("^[A-HJ-NPR-Z0-9]{17}$")) {
                throw new ChassiInvalidoException("Chassi inválido");
            }
            if (veiculoRepositoryPort.existePorChassiEIdDiferente(chassi, id)) {
                throw new ChassiJaCadastradoException("Já existe um Veículo com esse Chassi Cadastrado!");
            }
        }

        // Validar formato e unicidade do Renavam se mudou
        String renavam = input.getRenavam();
        if (renavam != null && !renavam.isBlank()) {
            if (!isValidRenavam(renavam)) {
                throw new RenavamInvalidoException("RENAVAM inválido");
            }
            if (veiculoRepositoryPort.existePorRenavamEIdDiferente(renavam, id)) {
                throw new RenavamJaCadastradoException("Já existe um Veículo com esse Renavam Cadastrado!");
            }
        }

        // Atualizar campos da entidade
        veiculo.atualizar(
            input.getPlaca(),
            input.getChassi(),
            input.getRenavam(),
            input.getMarca(),
            input.getModelo(),
            input.getAnoFabricacao(),
            input.getAnoModelo(),
            input.getCor(),
            input.getQuilometragem(),
            input.getTipoCombustivel(),
            input.getCategoriaVeiculo()
        );

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
