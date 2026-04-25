package br.com.autocenterfiap.orcamento.service;

import br.com.autocenterfiap.orcamento.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.model.Orcamento;
import br.com.autocenterfiap.orcamento.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrcamentoService {
    private final OrcamentoRepository orcamentoRepository;

    public void gerar(Long ordemServicoId) {
        orcamentoRepository.save(new Orcamento());
    }

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
}
