package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemProdutoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper.OSItemProdutoJpaMapper;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemProdutoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Optional;

public class OSItemProdutoRepositoryJpaAdapter implements OSItemProdutoRepositoryPort {

    private final OSItemProdutoJpaRepository osItemProdutoJpaRepository;

    public OSItemProdutoRepositoryJpaAdapter(OSItemProdutoJpaRepository osItemProdutoJpaRepository) {
        this.osItemProdutoJpaRepository = osItemProdutoJpaRepository;
    }

    @Override
    public PageResult<OSItemProduto> findByOrdemServicoId(Long ordemServicoId, PaginationRequest pagination) {

        PageRequest pageRequest = PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                Sort.by(Sort.Direction.fromString(pagination.getSortDirection()), pagination.getSortBy())
        );

        Page<OSItemProdutoJpaEntity> page = this.osItemProdutoJpaRepository
                .findByOrdemServicoJpaEntityId(ordemServicoId, pageRequest);

        return new PageResult<>(
                page.getContent().stream().map(OSItemProdutoJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Optional<OSItemProduto> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId) {
        return this.osItemProdutoJpaRepository
                .findByOrdemServicoJpaEntityIdAndProdutoId(ordemServicoId, produtoId)
                .map(OSItemProdutoJpaMapper::toDomain);
    }

    @Override
    public BigDecimal calcularTotalProdutosPorOS(Long ordemServicoId) {
        return this.osItemProdutoJpaRepository.calcularTotalProdutosPorOS(ordemServicoId);
    }

    @Override
    public OSItemProduto save(OSItemProduto item) {
        OSItemProdutoJpaEntity itemJpaEntity = OSItemProdutoJpaMapper.toJpa(item);
        OSItemProdutoJpaEntity salvo = this.osItemProdutoJpaRepository.save(itemJpaEntity);

        return OSItemProdutoJpaMapper.toDomain(salvo);
    }
}
