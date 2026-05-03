package br.com.autocenterfiap.cliente.service;

import br.com.autocenterfiap.cliente.dto.ClienteDTO;
import br.com.autocenterfiap.cliente.dto.ClienteResponseDTO;
import br.com.autocenterfiap.cliente.exception.*;
import br.com.autocenterfiap.cliente.mapper.ClienteMapper;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.cliente.validator.DocumentoValidator;
import br.com.autocenterfiap.cliente.validator.DocumentoValidatorFactory;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteMapper clienteMapper;

    public Page<ClienteResponseDTO> listarTodos(Pageable pageable) {
        log.info("Listando clientes com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Cliente> clientes = clienteRepository.findAll(pageable);

        log.info("Total de {} clientes encontrados na página {} de {}",
                clientes.getNumberOfElements(),
                clientes.getNumber() + 1,
                clientes.getTotalPages());

        return clientes.map(clienteMapper::toResponseDTO);
    }

    public ResponseEntity<ClienteResponseDTO> buscarPorId(Long id) {
        log.info("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .map(clienteMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<ClienteResponseDTO> buscarPorDocumento(String documento) {
        log.info("Buscando cliente por documento: {}", documento);
        return clienteRepository.findByDocumento(documento)
                .map(clienteMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ClienteResponseDTO criar(ClienteDTO clienteDTO) {
        log.info("Iniciando criação de cliente: tipo={}, documento={}",
            clienteDTO.getTipoCliente(), clienteDTO.getDocumento());

        Cliente cliente = clienteMapper.toEntity(clienteDTO);

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

            return clienteMapper.toResponseDTO(clienteSalvo);

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
    public ClienteResponseDTO atualizar(Long id, ClienteDTO clienteDTO) {
        log.info("Iniciando atualização do cliente ID: {}", id);

        Cliente cliente = clienteMapper.toEntity(clienteDTO);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar cliente inexistente: ID={}", id);
                    return new ClienteNaoEncontradoException(id);
                });

        // REGRA: Documento não pode ser alterado após cadastro
        if (!clienteExistente.getDocumento().equals(cliente.getDocumento())) {
            log.warn("Tentativa de alterar documento do cliente ID={}: antigo={}, novo={}",
                id, clienteExistente.getDocumento(), cliente.getDocumento());
            throw new DocumentoNaoPodeSerAlteradoException();
        }

        // REGRA: Tipo de cliente não pode ser alterado (pois está atrelado ao documento)
        if (clienteExistente.getTipoCliente() != cliente.getTipoCliente()) {
            log.warn("Tentativa de alterar tipo de cliente ID={}: antigo={}, novo={}",
                id, clienteExistente.getTipoCliente(), cliente.getTipoCliente());
            throw new IllegalArgumentException("O tipo de cliente não pode ser alterado.");
        }

        // Validar se email não está sendo usado por outro cliente
        if (!clienteExistente.getEmail().equals(cliente.getEmail())) {
            log.info("Email sendo alterado para cliente ID={}: antigo={}, novo={}",
                id, clienteExistente.getEmail(), cliente.getEmail());
            if (existePorEmail(cliente.getEmail())) {
                log.warn("Tentativa de atualizar para email já cadastrado: {}", cliente.getEmail());
                throw new EmailJaCadastradoException(cliente.getEmail());
            }
        }

        // Atualiza os dados
        cliente.setId(id);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente atualizado com sucesso: ID={}, Nome={}",
            clienteSalvo.getId(), clienteSalvo.getNome());

        return clienteMapper.toResponseDTO(clienteSalvo);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão do cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de deletar cliente inexistente: ID={}", id);
                    return new ClienteNaoEncontradoException(id);
                });

        boolean clienteEmUso = ordemServicoRepository.existsByClienteId(id);
        if (clienteEmUso) {
            log.warn("Tentativa de deletar cliente em uso: ID={}", id);
            throw new ClienteEmUsoException("Não é possível deletar o cliente, pois ele está associado a uma ordem de serviço ativa.");
        }

        log.info("Deletando cliente: ID={}, Nome={}, Documento={}",
            cliente.getId(), cliente.getNome(), cliente.getDocumento());
        clienteRepository.delete(cliente);
        log.info("Cliente deletado com sucesso: ID={}", id);
    }
}
