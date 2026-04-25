package br.com.autocenterfiap.orcamento.service;

import br.com.autocenterfiap.orcamento.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.orcamento.repository.OrcamentoRepository;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.repository.entity.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrcamentoService {
    private final OrcamentoRepository orcamentoRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public void aprovar(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(orcamentoId));
        orcamento.aprovar();
        orcamentoRepository.save(orcamento);
    }

    public void reprovar(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(orcamentoId));
        orcamento.reprovar();
        orcamentoRepository.save(orcamento);
    }

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
                .valorTotal(os.getValorTotal())
                .build();
        return orcamentoRepository.save(orcamento);
    }
}
