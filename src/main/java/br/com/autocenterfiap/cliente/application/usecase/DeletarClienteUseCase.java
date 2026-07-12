package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteEmUsoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeletarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public DeletarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public void executar(Long id) {
        log.info("Iniciando exclusão do cliente ID: {}", id);

        Cliente cliente = clienteRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> {
                log.warn("Tentativa de deletar cliente inexistente: ID={}", id);
                return new ClienteNaoEncontradoException(id);
            });

        if (clienteRepositoryPort.existeOrdemServicoAssociada(id)) {
            log.warn("Tentativa de deletar cliente em uso: ID={}", id);
            throw new ClienteEmUsoException(
                "Não é possível deletar o cliente, pois ele está associado a uma ordem de serviço ativa."
            );
        }

        log.info("Deletando cliente: ID={}, Nome={}, Documento={}",
            cliente.getId(), cliente.getNome(), cliente.getDocumento());
        clienteRepositoryPort.deletarPorId(id);
        log.info("Cliente deletado com sucesso: ID={}", id);
    }
}

