package br.com.autocenterfiap.cliente.service;

import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoInvalidoException;
import br.com.autocenterfiap.cliente.exception.DocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.exception.EmailJaCadastradoException;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.cliente.validator.CnpjValidator;
import br.com.autocenterfiap.cliente.validator.CpfValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorDocumento(String documento) {
        return clienteRepository.findByDocumento(documento);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public boolean existePorDocumento(String documento) {
        return clienteRepository.existsByDocumento(documento);
    }

    public boolean existePorEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        // Validar consistência entre tipo de cliente e documento
        validarTipoClienteDocumento(cliente);

        // Validar se o documento (CPF/CNPJ) é válido
        validarDocumento(cliente);

        // Validações de negócio
        if (existePorDocumento(cliente.getDocumento())) {
            throw new DocumentoJaCadastradoException(cliente.getDocumento());
        }

        if (existePorEmail(cliente.getEmail())) {
            throw new EmailJaCadastradoException(cliente.getEmail());
        }

        // Garante que é um novo registro
        cliente.setId(null);

        return clienteRepository.save(cliente);
    }

    private void validarTipoClienteDocumento(Cliente cliente) {
        int tamanhoDocumento = cliente.getDocumento().length();

        if (cliente.getTipoCliente() == TipoCliente.PESSOA_FISICA && tamanhoDocumento != 11) {
            throw new IllegalArgumentException("Para Pessoa Física, o documento (CPF) deve ter 11 dígitos");
        }

        if (cliente.getTipoCliente() == TipoCliente.PESSOA_JURIDICA && tamanhoDocumento != 14) {
            throw new IllegalArgumentException("Para Pessoa Jurídica, o documento (CNPJ) deve ter 14 dígitos");
        }
    }

    private void validarDocumento(Cliente cliente) {
        String documento = cliente.getDocumento();
        TipoCliente tipo = cliente.getTipoCliente();

        if (tipo == TipoCliente.PESSOA_FISICA) {
            if (!CpfValidator.isValid(documento)) {
                throw new DocumentoInvalidoException("CPF", documento);
            }
        } else if (tipo == TipoCliente.PESSOA_JURIDICA) {
            if (!CnpjValidator.isValid(documento)) {
                throw new DocumentoInvalidoException("CNPJ", documento);
            }
        }
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        // REGRA: Documento não pode ser alterado após cadastro
        if (!clienteExistente.getDocumento().equals(clienteAtualizado.getDocumento())) {
            throw new DocumentoNaoPodeSerAlteradoException();
        }

        // REGRA: Tipo de cliente não pode ser alterado (pois está atrelado ao documento)
        if (clienteExistente.getTipoCliente() != clienteAtualizado.getTipoCliente()) {
            throw new IllegalArgumentException("O tipo de cliente não pode ser alterado.");
        }

        // Validar se email não está sendo usado por outro cliente
        if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
            if (existePorEmail(clienteAtualizado.getEmail())) {
                throw new EmailJaCadastradoException(clienteAtualizado.getEmail());
            }
        }

        // Atualiza os dados
        clienteAtualizado.setId(id);
        return clienteRepository.save(clienteAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        clienteRepository.delete(cliente);
    }
}
