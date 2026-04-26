package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.ordemservico.dto.OSItemProdutoDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemProdutoResponseDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemServicoDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.validator.OrdemServicoValidator;
import br.com.autocenterfiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepositoryl;
    private final VeiculoRepository veiculoRepository;
    private final List<OrdemServicoValidator> validators;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, ClienteRepository clienteRepositoryl, VeiculoRepository veiculoRepository, List<OrdemServicoValidator> validators) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepositoryl = clienteRepositoryl;
        this.veiculoRepository = veiculoRepository;
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
        // Rodar os Validators da OS
        validators.forEach(v -> v.validate(dto));

        Veiculo veiculo = findVeiculoById(dto.veiculoId());
        Cliente cliente = findClienteById(dto.clienteId());
        OrdemServico ordemServico = new OrdemServico(dto, veiculo, cliente);
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

    // ========================================
    // Sub-recursos: Produtos
    // ========================================

    @Transactional
    public OSItemProdutoResponseDTO adicionarProduto(Long ordemServicoId, OSItemProdutoDTO dto) {
        OrdemServico ordemServico = findById(ordemServicoId);
        // TODO: Mapear e adicionar OSItemProduto à OrdemServico
        return null;
    }

    @Transactional
    public OSItemProdutoResponseDTO atualizarProduto(Long ordemServicoId, Long produtoItemId, OSItemProdutoDTO dto) {
        OrdemServico ordemServico = findById(ordemServicoId);
        // TODO: Buscar o item, atualizar e salvar
        return null;
    }

    @Transactional
    public void removerProduto(Long ordemServicoId, Long produtoItemId) {
        OrdemServico ordemServico = findById(ordemServicoId);
        // TODO: Remover o item da lista e salvar
    }

    // ========================================
    // Sub-recursos: Serviços
    // ========================================

    @Transactional
    public OSItemServicoResponseDTO adicionarServico(Long ordemServicoId, OSItemServicoDTO dto) {
        OrdemServico ordemServico = findById(ordemServicoId);
        // TODO: Mapear e adicionar OSItemServico à OrdemServico
        return null;
    }

    @Transactional
    public OSItemServicoResponseDTO atualizarServico(Long ordemServicoId, Long servicoItemId, OSItemServicoDTO dto) {
        OrdemServico ordemServico = findById(ordemServicoId);
        // TODO: Buscar o item, atualizar e salvar
        return null;
    }

    @Transactional
    public void removerServico(Long ordemServicoId, Long servicoItemId) {
        OrdemServico ordemServico = findById(ordemServicoId);
        // TODO: Remover o item da lista e salvar
    }

    // Funções auxiliares para buscar entidades relacionadas e validar a existência de OS

    private OrdemServico findById(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada com ID: " + id));
    }

    private OrdemServico findByNumeroOrdemServico(Long numeroOrdemServico) {
        return ordemServicoRepository.findByNumeroOrdemServico(numeroOrdemServico)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada com Número: " + numeroOrdemServico));
    }

    private Veiculo findVeiculoById(Long veiculoId) {
        return veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado com ID: " + veiculoId));
    }

    private Cliente findClienteById(Long clienteId) {
        return clienteRepositoryl.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + clienteId));
    }


}
