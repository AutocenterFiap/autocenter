package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.dto.ClienteOutput;
import br.com.autocenterfiap.cliente.application.dto.PageResult;
import br.com.autocenterfiap.cliente.application.dto.PaginationRequest;
import br.com.autocenterfiap.cliente.application.mapper.ClienteApplicationMapper;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListarClientesUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public ListarClientesUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public PageResult<ClienteOutput> executar(PaginationRequest pagination) {
        log.info("Listando clientes - Página: {}, Tamanho: {}",
            pagination.getPageNumber(), pagination.getPageSize());

        PageResult<ClienteOutput> result = clienteRepositoryPort.listarTodos(pagination)
            .map(ClienteApplicationMapper::toOutput);

        log.info("Total de {} clientes encontrados na página {} de {}",
            result.getNumberOfElements(),
            result.getPageNumber() + 1,
            result.getTotalPages());

        return result;
    }
}

