package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper.OSItemServicoJpaMapper;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemServicoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class OSItemServicoRepositoryJpaAdapter implements OSItemServicoRepositoryPort {

    private final OSItemServicoJpaRepository osItemServicoJpaRepository;

    public OSItemServicoRepositoryJpaAdapter(OSItemServicoJpaRepository osItemServicoJpaRepository) {
        this.osItemServicoJpaRepository = osItemServicoJpaRepository;
    }

    @Override
    public PageResult<OSItemServico> findAllFinalizados(PaginationRequest pagination) {

        PageRequest pageRequest = PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                Sort.by(Sort.Direction.fromString(pagination.getSortDirection()), pagination.getSortBy())
        );

        Page<OSItemServicoJpaEntity> page = this.osItemServicoJpaRepository.findAllFinalizados(pageRequest);

        return new PageResult<>(
                page.getContent().stream().map(OSItemServicoJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public PageResult<OSItemServico> findByOrdemServicoId(Long ordermServicoId, PaginationRequest pagination) {

        PageRequest pageRequest = PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                Sort.by(Sort.Direction.fromString(pagination.getSortDirection()), pagination.getSortBy())
        );

        Page<OSItemServicoJpaEntity> page = this.osItemServicoJpaRepository
                .findByOrdemServicoJpaEntityId(ordermServicoId, pageRequest);

        return new PageResult<>(
                page.getContent().stream().map(OSItemServicoJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Optional<OSItemServico> findByServicoIdAndOrdemServicoId(Long servicoId, Long ordermServicoId) {
        return this.osItemServicoJpaRepository
                .findByServicoIdAndOrdemServicoJpaEntityId(servicoId, ordermServicoId)
                .map(OSItemServicoJpaMapper::toDomain);
    }

    @Override
    public boolean existsByServicoId(Long servicoId) {
        return this.osItemServicoJpaRepository.existsByServicoId(servicoId);
    }

    @Override
    public OSItemServico save(OSItemServico item) {
        OSItemServicoJpaEntity itemJpaEntity = OSItemServicoJpaMapper.toJpa(item);
        OSItemServicoJpaEntity salvo = this.osItemServicoJpaRepository.save(itemJpaEntity);

        return OSItemServicoJpaMapper.toDomain(salvo);
    }

    @Override
    public void deleteById(Long id) {
        this.osItemServicoJpaRepository.deleteByIdDirect(id);
    }
}
