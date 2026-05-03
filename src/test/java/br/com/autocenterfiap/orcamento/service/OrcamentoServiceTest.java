package br.com.autocenterfiap.orcamento.service;

import br.com.autocenterfiap.orcamento.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.repository.OrcamentoRepository;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.model.OSItemProduto;
import br.com.autocenterfiap.ordemservico.model.OSItemServico;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.produto.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.autocenterfiap.orcamento.enums.StatusOrcamento.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private OrcamentoService orcamentoService;

    private Orcamento orcamento;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {

        Produto produto = new Produto();
        produto.setPrecoUnitario(new BigDecimal("200.00"));

        OSItemProduto itemProduto = new OSItemProduto();
        itemProduto.setProduto(produto);
        itemProduto.setQuantidade(1);
        itemProduto.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());

        OSItemServico itemServico = new OSItemServico();
        itemServico.setValorItemServico(new BigDecimal("300.00"));

        ordemServico = OrdemServico.builder()
                .id(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOS(StatusOS.AGUARDANDO_APROVACAO)
                .osItensProdutos(new ArrayList<>(List.of(itemProduto)))
                .osItensServicos(new ArrayList<>(List.of(itemServico)))
                .build();

        orcamento = Orcamento.builder()
                .id(1L)
                .ordemServico(ordemServico)
                .statusOrcamento(AGUARDANDO_APROVACAO)
                .valorTotal(BigDecimal.valueOf(500))
                .build();
    }

    @Test
    @DisplayName("Deve aprovar orçamento existente")
    void deveAprovarOrcamento() {
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = orcamentoService.aprovar(1L);

        assertEquals(APROVADO, resultado.getStatusOrcamento());
        verify(orcamentoRepository).save(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao aprovar orçamento inexistente")
    void deveLancarExcecaoAoAprovarOrcamentoInexistente() {
        when(orcamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> orcamentoService.aprovar(99L));
    }

    @Test
    @DisplayName("Deve reprovar orçamento existente")
    void deveReprovarOrcamento() {
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = orcamentoService.reprovar(1L);

        assertEquals(REPROVADO, resultado.getStatusOrcamento());
        verify(orcamentoRepository).save(resultado);
    }

    @Test
    @DisplayName("Deve gerar orçamento para OS aguardando aprovação com valor calculado")
    void deveGerarOrcamentoParaOS() {
        when(ordemServicoRepository.findByStatus(StatusOS.AGUARDANDO_APROVACAO))
                .thenReturn(List.of(ordemServico));
        when(orcamentoRepository.buscarOrcamentoAguardandoAprovacaoPorOS(ordemServico))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        orcamentoService.gerarOrcamento();

        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoRepository).save(captor.capture());
        // produto: R$ 200,00 x 1 + serviço: R$ 300,00 = R$ 500,00
        assertEquals(0, new BigDecimal("500.00").compareTo(captor.getValue().getValorTotal()));
        assertEquals(AGUARDANDO_APROVACAO, captor.getValue().getStatusOrcamento());
    }

    @Test
    @DisplayName("Não deve gerar novo orçamento se já existir aguardando aprovação")
    void naoDeveGerarNovoOrcamentoSeJaExistir() {
        when(ordemServicoRepository.findByStatus(StatusOS.AGUARDANDO_APROVACAO))
                .thenReturn(List.of(ordemServico));
        when(orcamentoRepository.buscarOrcamentoAguardandoAprovacaoPorOS(ordemServico))
                .thenReturn(Optional.of(orcamento));

        orcamentoService.gerarOrcamento();

        verify(orcamentoRepository, never()).save(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve encontrar orçamento por ID e aprovar")
    void deveEncontrarOrcamentoPorIdEAprovar() {
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento resultado = orcamentoService.findById(1L);

        assertEquals(APROVADO, resultado.getStatusOrcamento());
        verify(orcamentoRepository).save(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar orçamento inexistente")
    void deveLancarExcecaoAoBuscarOrcamentoInexistente() {
        when(orcamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class, () -> orcamentoService.findById(99L));
    }
}
