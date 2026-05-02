package br.com.autocenterfiap.orcamento.service;

import br.com.autocenterfiap.orcamento.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.repository.OrcamentoRepository;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.util.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.autocenterfiap.orcamento.enums.StatusOrcamento.AGUARDANDO_APROVACAO;

@Service
@RequiredArgsConstructor
public class OrcamentoService {
    private final OrcamentoRepository orcamentoRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    @Transactional
    public Orcamento aprovar(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(orcamentoId));
        orcamento.aprovar();
        orcamento.getOrdemServico().setStatusOS(StatusOS.APROVADA);
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public Orcamento reprovar(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(orcamentoId));
        orcamento.reprovar();
        orcamento.getOrdemServico().setStatusOS(StatusOS.CANCELADA);
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public void gerarOrcamento() {
        List<OrdemServico> ordens = ordemServicoRepository.findByStatus(StatusOS.AGUARDANDO_APROVACAO);

        ordens.forEach(os ->
                orcamentoRepository.buscarOrcamentoAguardandoAprovacaoPorOS(os)
                        .orElseGet(() -> salvarOrcamento(os))
        );
    }

    private Orcamento salvarOrcamento(OrdemServico os) {
        Orcamento orcamento = Orcamento.builder()
                .ordemServico(os)
                .statusOrcamento(AGUARDANDO_APROVACAO)
                .valorTotal(Util.calcularValorTotal(os))
                .build();
        return orcamentoRepository.save(orcamento);
    }

    public Orcamento findById(Long id){
        Orcamento orcamento = orcamentoRepository
                .findById(id)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(id));
        orcamento.aprovar();
        return orcamentoRepository.save(orcamento);
    }

    public Page<Orcamento> listarTodos(StatusOrcamento statusOrcamento, Pageable pageable) {
        Page<Orcamento> orcamentos = orcamentoRepository.findByStatusOrcamento(statusOrcamento, pageable);
        if (orcamentos.isEmpty()) {
            return Page.empty(pageable);
        }
        return orcamentos;
    }
}
