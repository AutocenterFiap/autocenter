package br.com.autocenterfiap.orcamento.domain.entity;

import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Orcamento - Testes Unitários da Entidade de Domínio")
class OrcamentoTest {

    @Test
    @DisplayName("Deve aprovar orçamento mudando status para APROVADO")
    void deveAprovarOrcamento() {
        Orcamento orcamento = Orcamento.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        orcamento.aprovar();

        assertEquals(StatusOrcamento.APROVADO, orcamento.getStatusOrcamento());
    }

    @Test
    @DisplayName("Deve reprovar orçamento mudando status para REPROVADO")
    void deveReprovarOrcamento() {
        Orcamento orcamento = Orcamento.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        orcamento.reprovar();

        assertEquals(StatusOrcamento.REPROVADO, orcamento.getStatusOrcamento());
    }

    @Test
    @DisplayName("Deve passar na validação com dados válidos")
    void devePassarNaValidacaoComDadosValidos() {
        Orcamento orcamento = Orcamento.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        assertDoesNotThrow(orcamento::validarDominio);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando ordemServicoId for nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdNulo() {
        Orcamento orcamento = Orcamento.builder()
                .ordemServicoId(null)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        assertThrows(IllegalArgumentException.class, orcamento::validarDominio);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando valorTotal for zero ou negativo")
    void deveLancarExcecaoQuandoValorTotalZeroOuNegativo() {
        Orcamento orcamento = Orcamento.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.ZERO)
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        assertThrows(IllegalArgumentException.class, orcamento::validarDominio);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando status for nulo")
    void deveLancarExcecaoQuandoStatusNulo() {
        Orcamento orcamento = Orcamento.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(null)
                .build();

        assertThrows(IllegalArgumentException.class, orcamento::validarDominio);
    }
}
