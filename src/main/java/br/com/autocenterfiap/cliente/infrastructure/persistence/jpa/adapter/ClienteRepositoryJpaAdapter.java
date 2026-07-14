package br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.cliente.application.dto.PageResult;
import br.com.autocenterfiap.cliente.application.dto.PaginationRequest;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.ClienteJpaEntity;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.mapper.ClienteJpaMapper;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.repository.ClienteJpaRepository;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryJpaAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final OrdemServicoJpaRepository ordemServicoJpaRepository;

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteJpaRepository.findById(id)
            .map(ClienteJpaMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        return clienteJpaRepository.findByDocumento(documento)
            .map(ClienteJpaMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteJpaRepository.findByEmail(email)
            .map(ClienteJpaMapper::toDomain);
    }

    @Override
    public boolean existePorDocumento(String documento) {
        return clienteJpaRepository.existsByDocumento(documento);
    }

    @Override
    public boolean existePorEmail(String email) {
        return clienteJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existePorId(Long id) {
        return clienteJpaRepository.existsById(id);
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteJpaEntity jpaEntity = ClienteJpaMapper.toJpa(cliente);
        ClienteJpaEntity savedEntity = clienteJpaRepository.save(jpaEntity);
        return ClienteJpaMapper.toDomain(savedEntity);
    }

    @Override
    public void deletarPorId(Long id) {
        clienteJpaRepository.deleteById(id);
    }

    @Override
    public boolean existeOrdemServicoAssociada(Long clienteId) {
        return ordemServicoJpaRepository.existsByClienteId(clienteId);
    }

    @Override
    public PageResult<Cliente> listarTodos(PaginationRequest pagination) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(pagination.getSortDirection())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        PageRequest pageRequest = PageRequest.of(
            pagination.getPageNumber(),
            pagination.getPageSize(),
            Sort.by(direction, pagination.getSortBy())
        );

        Page<ClienteJpaEntity> pageJpa = clienteJpaRepository.findAll(pageRequest);

        return new PageResult<>(
            pageJpa.getContent().stream()
                .map(ClienteJpaMapper::toDomain)
                .toList(),
            pageJpa.getNumber(),
            pageJpa.getSize(),
            pageJpa.getTotalElements()
        );
    }
}

