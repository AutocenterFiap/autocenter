package br.com.autocenterfiap.orcamento.repository;

import br.com.autocenterfiap.orcamento.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrcamentoRepositoryTest {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Test
    void deveBuscarOrcamentoAguardandoAprovacaoPorOS() {
        // cria uma ordem de serviço
        OrdemServico os = new OrdemServico();
        os.setNumeroOrdemServico(123L);
        os.setValorTotal(BigDecimal.valueOf(1000));
        os.setStatusOS(StatusOS.RECEBIDA);
        ordemServicoRepository.save(os);

        // cria um orçamento vinculado à OS
        Orcamento orcamento = new Orcamento();
        orcamento.setOrdemServico(os);
        orcamento.setValorTotal(BigDecimal.valueOf(1000));
        orcamento.setStatusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO);
        orcamentoRepository.save(orcamento);

        Optional<Orcamento> resultado = orcamentoRepository.buscarOrcamentoAguardandoAprovacaoPorOS(os);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getStatusOrcamento()).isEqualTo(StatusOrcamento.AGUARDANDO_APROVACAO);
        assertThat(resultado.get().getOrdemServico().getId()).isEqualTo(os.getId());
    }
}
