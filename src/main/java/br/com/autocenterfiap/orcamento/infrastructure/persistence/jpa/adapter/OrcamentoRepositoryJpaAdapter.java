package br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.orcamento.application.dto.PageResult;
import br.com.autocenterfiap.orcamento.application.dto.PaginationRequest;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.entity.OrcamentoJpaEntity;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.mapper.OrcamentoJpaMapper;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.repository.OrcamentoJpaRepository;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@RequiredArgsConstructor
public class OrcamentoRepositoryJpaAdapter implements OrcamentoRepositoryPort {

    private final OrcamentoJpaRepository orcamentoJpaRepository;
    private final OrdemServicoJpaRepository ordemServicoJpaRepository;

    @Override
    public Optional<Orcamento> buscarPorId(Long id) {
        return this.orcamentoJpaRepository.findById(id)
                .map(OrcamentoJpaMapper::toDomain);
    }

    @Override
    public Optional<Orcamento> buscarOrcamentoAguardandoAprovacaoPorOS(Long os) {
        OrdemServicoJpaEntity ordemServicoJpaEntity = ordemServicoJpaRepository.findById(os).orElseThrow();
        return this.orcamentoJpaRepository.buscarOrcamentoAguardandoAprovacaoPorOS(ordemServicoJpaEntity)
                .map(OrcamentoJpaMapper::toDomain);
    }

    @Override
    public PageResult<Orcamento> buscarPorStatusOrcamento(StatusOrcamento status, PaginationRequest pagination) {

        PageRequest pageRequest = PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                Sort.by(Sort.Direction.fromString(pagination.getSortDirection()), pagination.getSortBy())
        );

        Page<OrcamentoJpaEntity> page = this.orcamentoJpaRepository.findByStatusOrcamento(status, pageRequest);
        return new PageResult<>(
                page.getContent().stream().map(OrcamentoJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        OrdemServicoJpaEntity ordemServicoJpaEntity = ordemServicoJpaRepository.findById(orcamento.getOrdemServicoId()).orElseThrow();
        OrcamentoJpaEntity orcamentoJpaEntity = OrcamentoJpaMapper.toJpa(orcamento, ordemServicoJpaEntity);

        OrcamentoJpaEntity salvo = this.orcamentoJpaRepository.save(orcamentoJpaEntity);
        return OrcamentoJpaMapper.toDomain(salvo);
    }
}
