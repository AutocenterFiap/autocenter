package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidator implements OrdemServicoValidator{
    private final ClienteRepositoryPort clienteRepositoryPort;

    public ClienteValidator(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    public void validate(CriarOrdemServicoInput dto) {
        if(!clienteRepositoryPort.existePorId(dto.clienteId())){
            throw new ClienteNaoEncontradoException("Cliente com ID " + dto.clienteId() + " não encontrado!");
        }
    }
}
