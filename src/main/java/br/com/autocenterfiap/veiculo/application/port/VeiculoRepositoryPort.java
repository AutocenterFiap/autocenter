package br.com.autocenterfiap.veiculo.application.port;

import java.util.Optional;

import br.com.autocenterfiap.veiculo.application.dto.PageResult;
import br.com.autocenterfiap.veiculo.application.dto.PaginationRequest;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;

public interface VeiculoRepositoryPort {

    Optional<Veiculo> buscarPorId(Long id);

    Optional<Veiculo> buscarPorPlaca(String placa);

    boolean existePorPlaca(String placa);

    boolean existePorPlacaEIdDiferente(String placa, Long id);

    boolean existePorChassi(String chassi);

    boolean existePorChassiEIdDiferente(String chassi, Long id);

    boolean existePorRenavam(String renavam);

    boolean existePorRenavamEIdDiferente(String renavam, Long id);

    boolean existeOrdemServicoAssociada(Long veiculoId);

    Veiculo salvar(Veiculo veiculo);

    void deletarPorId(Long id);

    PageResult<Veiculo> listarTodos(PaginationRequest pagination);
}
