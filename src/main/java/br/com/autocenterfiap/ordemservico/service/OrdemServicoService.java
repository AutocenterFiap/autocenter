package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.validator.OrdemServicoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final List<OrdemServicoValidator> validators;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, List<OrdemServicoValidator> validators) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.validators = validators;
    }

    public Page<OrdemServicoResponseDTO> listarTodos(Pageable pageable) {
        return ordemServicoRepository.findAll(pageable).map(OrdemServicoResponseDTO::new);
    }

    public OrdemServicoResponseDTO buscarPorId(Long id) {
        OrdemServico ordemServico = findById(id);
        return new OrdemServicoResponseDTO(ordemServico);
    }

    public OrdemServicoResponseDTO buscarPorNumeroOrdemServico(Long numeroOs) {
        OrdemServico ordemServico = findByNumeroOrdemServico(numeroOs);
        return new OrdemServicoResponseDTO(ordemServico);
    }

    public OrdemServicoResponseDTO criar(OrdemServicoDTO dto) {

        // Criar os Validators da OS
        validators.forEach(v -> v.validate());

        OrdemServico ordemServico = new OrdemServico(dto);
        ordemServico = ordemServicoRepository.save(ordemServico);
        return new OrdemServicoResponseDTO(ordemServico);
    }

    @Transactional
    public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoDTO dto) {
        // Implementar
        return null;
    }

    @Transactional
    public void deletar(Long id) {
        OrdemServico ordemServico = findById(id);
        ordemServicoRepository.delete(ordemServico);
    }

    private OrdemServico findById(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada com ID: " + id));
    }

    private OrdemServico findByNumeroOrdemServico(Long numeroOrdemServico) {
        return ordemServicoRepository.findByNumeroOrdemServico(numeroOrdemServico)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada com Número: " + numeroOrdemServico));
    }


}
