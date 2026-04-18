package br.com.autocenterfiap.cliente.service;

import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoInvalidoException;
import br.com.autocenterfiap.cliente.exception.DocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.exception.EmailJaCadastradoException;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.cliente.validator.DocumentoValidator;
import br.com.autocenterfiap.cliente.validator.DocumentoValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        log.info("Listando todos os clientes");
        List<Cliente> clientes = clienteRepository.findAll();
        log.info("Total de {} clientes encontrados", clientes.size());
        return clientes;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        log.info("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorDocumento(String documento) {
        log.info("Buscando cliente por documento: {}", documento);
        return clienteRepository.findByDocumento(documento);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        log.info("Buscando cliente por email: {}", email);
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
        log.info("Iniciando criação de cliente: tipo={}, documento={}",
            cliente.getTipoCliente(), cliente.getDocumento());

        try {
            // Validar consistência entre tipo de cliente e documento
            validarTipoClienteDocumento(cliente);

            // Validar se o documento (CPF/CNPJ) é válido
            validarDocumento(cliente);

            // Validações de negócio
            if (existePorDocumento(cliente.getDocumento())) {
                log.warn("Tentativa de cadastro com documento já existente: {}", cliente.getDocumento());
                throw new DocumentoJaCadastradoException(cliente.getDocumento());
            }

            if (existePorEmail(cliente.getEmail())) {
                log.warn("Tentativa de cadastro com email já existente: {}", cliente.getEmail());
                throw new EmailJaCadastradoException(cliente.getEmail());
            }

            // Garante que é um novo registro
            cliente.setId(null);

            Cliente clienteSalvo = clienteRepository.save(cliente);
            log.info("Cliente criado com sucesso: ID={}, Nome={}, Documento={}",
                clienteSalvo.getId(), clienteSalvo.getNome(), clienteSalvo.getDocumento());

            return clienteSalvo;

        } catch (DocumentoInvalidoException | DocumentoJaCadastradoException | EmailJaCadastradoException e) {
            log.error("Erro ao criar cliente: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar cliente: documento={}, erro={}",
                cliente.getDocumento(), e.getMessage(), e);
            throw e;
        }
    }

    private void validarTipoClienteDocumento(Cliente cliente) {
        log.debug("Validando consistência entre tipo de cliente e tamanho do documento");
        DocumentoValidator validator = DocumentoValidatorFactory.getValidator(cliente.getTipoCliente());
        int tamanhoDocumento = cliente.getDocumento().length();
        int tamanhoEsperado = validator.getTamanhoEsperado();

        if (tamanhoDocumento != tamanhoEsperado) {
            String tipoDoc = validator.getTipoDocumento();
            String mensagem = String.format("Para %s, o documento (%s) deve ter %d dígitos",
                cliente.getTipoCliente().name(), tipoDoc, tamanhoEsperado);
            log.warn("Validação falhou: {}", mensagem);
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validarDocumento(Cliente cliente) {
        log.debug("Validando dígitos verificadores do documento");
        DocumentoValidator validator = DocumentoValidatorFactory.getValidator(cliente.getTipoCliente());

        if (!validator.isValid(cliente.getDocumento())) {
            log.warn("Documento inválido: tipo={}, documento={}",
                validator.getTipoDocumento(), cliente.getDocumento());
            throw new DocumentoInvalidoException(
                validator.getTipoDocumento(),
                cliente.getDocumento()
            );
        }
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        log.info("Iniciando atualização do cliente ID: {}", id);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar cliente inexistente: ID={}", id);
                    return new ClienteNaoEncontradoException(id);
                });

        // REGRA: Documento não pode ser alterado após cadastro
        if (!clienteExistente.getDocumento().equals(clienteAtualizado.getDocumento())) {
            log.warn("Tentativa de alterar documento do cliente ID={}: antigo={}, novo={}",
                id, clienteExistente.getDocumento(), clienteAtualizado.getDocumento());
            throw new DocumentoNaoPodeSerAlteradoException();
        }

        // REGRA: Tipo de cliente não pode ser alterado (pois está atrelado ao documento)
        if (clienteExistente.getTipoCliente() != clienteAtualizado.getTipoCliente()) {
            log.warn("Tentativa de alterar tipo de cliente ID={}: antigo={}, novo={}",
                id, clienteExistente.getTipoCliente(), clienteAtualizado.getTipoCliente());
            throw new IllegalArgumentException("O tipo de cliente não pode ser alterado.");
        }

        // Validar se email não está sendo usado por outro cliente
        if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail())) {
            log.info("Email sendo alterado para cliente ID={}: antigo={}, novo={}",
                id, clienteExistente.getEmail(), clienteAtualizado.getEmail());
            if (existePorEmail(clienteAtualizado.getEmail())) {
                log.warn("Tentativa de atualizar para email já cadastrado: {}", clienteAtualizado.getEmail());
                throw new EmailJaCadastradoException(clienteAtualizado.getEmail());
            }
        }

        // Atualiza os dados
        clienteAtualizado.setId(id);
        Cliente clienteSalvo = clienteRepository.save(clienteAtualizado);
        log.info("Cliente atualizado com sucesso: ID={}, Nome={}",
            clienteSalvo.getId(), clienteSalvo.getNome());

        return clienteSalvo;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão do cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de deletar cliente inexistente: ID={}", id);
                    return new ClienteNaoEncontradoException(id);
                });

        log.info("Deletando cliente: ID={}, Nome={}, Documento={}",
            cliente.getId(), cliente.getNome(), cliente.getDocumento());
        clienteRepository.delete(cliente);
        log.info("Cliente deletado com sucesso: ID={}", id);
    }
}
