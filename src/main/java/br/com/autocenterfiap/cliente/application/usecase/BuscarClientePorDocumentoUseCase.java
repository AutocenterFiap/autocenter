package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.dto.ClienteOutput;
import br.com.autocenterfiap.cliente.application.mapper.ClienteApplicationMapper;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuscarClientePorDocumentoUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public BuscarClientePorDocumentoUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public ClienteOutput executar(String documento) {
        log.info("Buscando cliente por documento: {}", documento);

        Cliente cliente = clienteRepositoryPort.buscarPorDocumento(documento)
            .orElseThrow(() -> {
                log.warn("Cliente não encontrado: documento={}", documento);
                return new ClienteNaoEncontradoException(documento);
            });

        log.info("Cliente encontrado: ID={}, Nome={}", cliente.getId(), cliente.getNome());
        return ClienteApplicationMapper.toOutput(cliente);
    }
}

