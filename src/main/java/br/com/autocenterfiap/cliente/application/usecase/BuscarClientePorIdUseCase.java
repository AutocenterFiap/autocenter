package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.dto.ClienteOutput;
import br.com.autocenterfiap.cliente.application.mapper.ClienteApplicationMapper;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuscarClientePorIdUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public BuscarClientePorIdUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public ClienteOutput executar(Long id) {
        log.info("Buscando cliente por ID: {}", id);

        Cliente cliente = clienteRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> {
                log.warn("Cliente não encontrado: ID={}", id);
                return new ClienteNaoEncontradoException(id);
            });

        log.info("Cliente encontrado: ID={}, Nome={}", cliente.getId(), cliente.getNome());
        return ClienteApplicationMapper.toOutput(cliente);
    }
}

