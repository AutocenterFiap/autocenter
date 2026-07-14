package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper.OrdemServicoJpaMapper;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public class OrdemServicoRepositoryJpaAdapter implements OrdemServicoRepositoryPort {

    private final OrdemServicoJpaRepository ordemServicoJpaRepository;

    public OrdemServicoRepositoryJpaAdapter(OrdemServicoJpaRepository ordemServicoJpaRepository) {
        this.ordemServicoJpaRepository = ordemServicoJpaRepository;
    }

    @Override
    public PageResult<OrdemServico> listarAtivasOrdenadasPorPrioridade(PaginationRequest pagination) {

        PageRequest pageRequest = PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize()
        );

        Page<OrdemServicoJpaEntity> page = this.ordemServicoJpaRepository.findAtivasOrdenadasPorPrioridade(pageRequest);
        return new PageResult<>(
                page.getContent().stream().map(OrdemServicoJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public PageResult<OrdemServico> findByStatus(StatusOS status, PaginationRequest pagination) {

        PageRequest pageRequest = PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                Sort.by(Sort.Direction.fromString(pagination.getSortDirection()), pagination.getSortBy())
        );

        Page<OrdemServicoJpaEntity> page = this.ordemServicoJpaRepository.findByStatus(status, pageRequest);
        return new PageResult<>(
                page.getContent().stream().map(OrdemServicoJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Optional<OrdemServico> findById(Long id) {
        return this.ordemServicoJpaRepository
                .findById(id)
                .map(OrdemServicoJpaMapper::toDomain);
    }

    @Override
    public Optional<OrdemServico> findByNumeroOrdemServico(Long numeroOrdemServico) {
        return this.ordemServicoJpaRepository
                .findByNumeroOrdemServico(numeroOrdemServico)
                .map(OrdemServicoJpaMapper::toDomain);
    }

    @Override
    public boolean existsByVeiculoId(Long veiculoId) {
        return this.ordemServicoJpaRepository.existsByVeiculoId(veiculoId);
    }

    @Override
    public boolean existsByClienteId(Long clienteId) {
        return this.ordemServicoJpaRepository.existsByClienteId(clienteId);
    }

    @Override
    public OrdemServico save(OrdemServico ordemServico) {

        OrdemServicoJpaEntity ordemServicoJpaEntity = OrdemServicoJpaMapper.toJpa(ordemServico);
        OrdemServicoJpaEntity salvo = this.ordemServicoJpaRepository.save(ordemServicoJpaEntity);

        return OrdemServicoJpaMapper.toDomain(salvo);
    }

    @Override
    public void delete(OrdemServico ordemServico) {
        this.ordemServicoJpaRepository.delete(OrdemServicoJpaMapper.toJpa(ordemServico));
    }

    @Override
    public boolean existsByVeiculoIdAndStatusOSIn(Long veiculoId, List<StatusOS> statusList) {
        return this.ordemServicoJpaRepository.existsByVeiculoIdAndStatusOSIn(veiculoId, statusList);
    }
}
