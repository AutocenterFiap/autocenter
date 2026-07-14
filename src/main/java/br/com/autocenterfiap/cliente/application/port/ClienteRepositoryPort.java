package br.com.autocenterfiap.cliente.application.port;

import br.com.autocenterfiap.cliente.application.dto.PageResult;
import br.com.autocenterfiap.cliente.application.dto.PaginationRequest;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;

import java.util.Optional;

public interface ClienteRepositoryPort {

    Optional<Cliente> buscarPorId(Long id);

    Optional<Cliente> buscarPorDocumento(String documento);

    Optional<Cliente> buscarPorEmail(String email);

    boolean existePorDocumento(String documento);

    boolean existePorEmail(String email);

    boolean existePorId(Long id);

    Cliente salvar(Cliente cliente);

    void deletarPorId(Long id);

    boolean existeOrdemServicoAssociada(Long clienteId);

    PageResult<Cliente> listarTodos(PaginationRequest pagination);
}

