package br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.veiculo.application.dto.PageResult;
import br.com.autocenterfiap.veiculo.application.dto.PaginationRequest;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity.VeiculoJpaEntity;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.mapper.VeiculoJpaMapper;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository.VeiculoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Optional;

@RequiredArgsConstructor
public class VeiculoRepositoryJpaAdapter implements VeiculoRepositoryPort {

    private final VeiculoJpaRepository veiculoJpaRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoJpaRepository.findById(id)
            .map(VeiculoJpaMapper::toDomain);
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return veiculoJpaRepository.findByPlaca(placa)
            .map(VeiculoJpaMapper::toDomain);
    }

    @Override
    public boolean existePorPlaca(String placa) {
        return veiculoJpaRepository.existsByPlaca(placa);
    }

    @Override
    public boolean existePorPlacaEIdDiferente(String placa, Long id) {
        return veiculoJpaRepository.existsByPlacaAndIdNot(placa, id);
    }

    @Override
    public boolean existePorChassi(String chassi) {
        return veiculoJpaRepository.existsByChassi(chassi);
    }

    @Override
    public boolean existePorChassiEIdDiferente(String chassi, Long id) {
        return veiculoJpaRepository.existsByChassiAndIdNot(chassi, id);
    }

    @Override
    public boolean existePorRenavam(String renavam) {
        return veiculoJpaRepository.existsByRenavam(renavam);
    }

    @Override
    public boolean existePorRenavamEIdDiferente(String renavam, Long id) {
        return veiculoJpaRepository.existsByRenavamAndIdNot(renavam, id);
    }

    @Override
    public boolean existeOrdemServicoAssociada(Long veiculoId) {
        return ordemServicoRepository.existsByVeiculoId(veiculoId);
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        VeiculoJpaEntity jpaEntity = VeiculoJpaMapper.toJpa(veiculo);
        VeiculoJpaEntity savedEntity = veiculoJpaRepository.save(jpaEntity);
        return VeiculoJpaMapper.toDomain(savedEntity);
    }

    @Override
    public void deletarPorId(Long id) {
        veiculoJpaRepository.deleteById(id);
    }

    @Override
    public PageResult<Veiculo> listarTodos(PaginationRequest pagination) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(pagination.getSortDirection())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        PageRequest pageRequest = PageRequest.of(
            pagination.getPageNumber(),
            pagination.getPageSize(),
            Sort.by(direction, pagination.getSortBy())
        );

        Page<VeiculoJpaEntity> pageJpa = veiculoJpaRepository.findAll(pageRequest);

        return new PageResult<>(
            pageJpa.getContent().stream()
                .map(VeiculoJpaMapper::toDomain)
                .toList(),
            pageJpa.getNumber(),
            pageJpa.getSize(),
            pageJpa.getTotalElements()
        );
    }
}
