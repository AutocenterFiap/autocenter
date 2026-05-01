package br.com.autocenterfiap.servico.service;

import br.com.autocenterfiap.servico.dto.ServicoDto;
import br.com.autocenterfiap.servico.dto.ServicoResponseDTO;
import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.exception.ServicoNaoEncontradoException;
import br.com.autocenterfiap.servico.mapper.ServicoMapper;
import br.com.autocenterfiap.servico.model.Servico;
import br.com.autocenterfiap.servico.repository.ServicoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ServicoService {

    @Autowired
    private ServicoRepository repository;

    @Autowired
    private ServicoMapper mapper;

    @Transactional
    public ServicoResponseDTO criar(ServicoDto servicoDto) {
        log.info("Iniciando criação de serviço: {}", servicoDto);
        Servico servico = mapper.toEntity(servicoDto);

        servico = repository.save(servico);
        log.info("Serviço criado com sucesso: ID={}, Descrição={}",
                servico.getId(), servico.getDescricao());

        return mapper.toServicoResponseDto(servico);
    }

    @Transactional(readOnly = true)
    public Page<ServicoResponseDTO> listarTodos(Pageable pageable) {
        log.info("Listando servicos com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Servico> servicos = repository.findAll(pageable);

        log.info("Total de {} clientes encontrados na página {} de {}",
                servicos.getNumberOfElements(),
                servicos.getNumber(),
                servicos.getTotalPages());

        return servicos.map(mapper::toServicoResponseDto);
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(Long id) {
        log.info("Iniciando busca de servico por id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    final String message = "Servico não encontrado de ID " + id;
                    log.info(message);
                    return new ServicoNaoEncontradoException(message);
                });
    }

    @Transactional
    public Page<ServicoResponseDTO> listaServicosPorStatus(StatusServico status, Pageable pageable) {
        log.info("Listando servicos por status: {} com paginação - Página: {}, Tamanho: {}",
                status, pageable.getPageNumber(), pageable.getPageSize());

        Page<Servico> servicos = repository.findAllByStatus(status, pageable);

        log.info("Total de {} clientes encontrados pelo status {} na página {} de {}",
                status,
                servicos.getNumberOfElements(),
                servicos.getNumber(),
                servicos.getTotalPages());

        return servicos.map(mapper::toServicoResponseDto);
    }

    @Transactional
    public ServicoResponseDTO atualizar(Long id, ServicoDto servicoAtualizadoDTO) {
        log.info("Iniciando atualização do servico por id: {}", id);

        Servico servicoExistente = buscarPorId(id);
        mapper.updateEntityFromDto(servicoAtualizadoDTO, servicoExistente);

        Servico servicoSalvo = repository.save(servicoExistente);
        log.info("Servico atualizado com sucesso: ID={}, Descricao={}",
                servicoSalvo.getId(), servicoSalvo.getDescricao());

        return mapper.toServicoResponseDto(servicoSalvo);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão do servico por id: {}", id);
        Servico servico = buscarPorId(id);

        log.info("Deletando cliente: ID={}", servico.getId());
        repository.delete(servico);
        log.info("Cliente deletado com sucesso: ID={}", id);
    }
}
