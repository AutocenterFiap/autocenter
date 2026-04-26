package br.com.autocenterfiap.ordemservico.validator;

import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidator implements OrdemServicoValidator{
    private final ClienteRepository clienteRepository;

    public ClienteValidator(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void validate(OrdemServicoDTO dto) {
        if(!clienteRepository.existsById(dto.clienteId())){
            throw new ClienteNaoEncontradoException("Cliente com ID " + dto.clienteId() + " não encontrado!");
        }
    }
}
