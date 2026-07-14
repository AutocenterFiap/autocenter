package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.dto.AtualizarClienteInput;
import br.com.autocenterfiap.cliente.application.dto.ClienteOutput;
import br.com.autocenterfiap.cliente.application.mapper.ClienteApplicationMapper;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.entity.Endereco;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteEmailJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AtualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public AtualizarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public ClienteOutput executar(Long id, AtualizarClienteInput input) {
        log.info("Iniciando atualização do cliente ID: {}", id);

        Cliente clienteExistente = clienteRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> {
                log.warn("Tentativa de atualizar cliente inexistente: ID={}", id);
                return new ClienteNaoEncontradoException(id);
            });

        if (input.getDocumento() != null && !clienteExistente.getDocumento().equals(input.getDocumento())) {
            log.warn("Tentativa de alterar documento do cliente ID={}", id);
            throw new ClienteDocumentoNaoPodeSerAlteradoException();
        }

        if (!clienteExistente.getEmail().equals(input.getEmail())) {
            if (clienteRepositoryPort.existePorEmail(input.getEmail())) {
                log.warn("Tentativa de atualizar para email já cadastrado: {}", input.getEmail());
                throw new ClienteEmailJaCadastradoException(input.getEmail());
            }
        }

        Endereco endereco = ClienteApplicationMapper.toEnderecoEntity(input);
        Cliente clienteAtualizado = clienteExistente.atualizar(
            input.getNome(),
            input.getEmail(),
            input.getTelefone(),
            endereco,
            input.getDataNascimento()
        );

        clienteAtualizado.validarDominio();

        Cliente clienteSalvo = clienteRepositoryPort.salvar(clienteAtualizado);
        log.info("Cliente atualizado com sucesso: ID={}, Nome={}",
            clienteSalvo.getId(), clienteSalvo.getNome());

        return ClienteApplicationMapper.toOutput(clienteSalvo);
    }
}

