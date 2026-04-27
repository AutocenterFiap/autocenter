package br.com.autocenterfiap.peca.model;

import br.com.autocenterfiap.peca.enums.StatusEstoque;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.exception.EstoqueInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Peca - Testes Unitários de Domínio")
class PecaTest {

    private Peca peca;

    @BeforeEach
    void setUp() {
        peca = new Peca();
        peca.setId(1L);
        peca.setCodigo("FO-001");
        peca.setNome("Filtro de Óleo");
        peca.setUnidadeMedida(UnidadeMedida.UNIT);
        peca.setPrecoUnitario(new BigDecimal("45.90"));
        peca.setQuantidadeEstoque(50);
        peca.setEstoqueMinimo(10);
        peca.setCategoria("Motor");
        peca.setAtivo(true);
    }

    @Test
    @DisplayName("decrementarEstoque deve reduzir o estoque corretamente")
    void decrementarEstoqueDeveReduzirCorretamente() {
        peca.decrementarEstoque(20);
        assertEquals(30, peca.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("decrementarEstoque deve lançar EstoqueInsuficienteException quando quantidade > estoque")
    void decrementarEstoqueDeveLancarExcecaoQuandoInsuficiente() {
        assertThrows(EstoqueInsuficienteException.class, () -> peca.decrementarEstoque(51));
    }

    @Test
    @DisplayName("decrementarEstoque deve lançar EstoqueInsuficienteException quando estoque é zero")
    void decrementarEstoqueDeveLancarExcecaoQuandoEstoqueZero() {
        peca.setQuantidadeEstoque(0);
        assertThrows(EstoqueInsuficienteException.class, () -> peca.decrementarEstoque(1));
    }

    @Test
    @DisplayName("decrementarEstoque deve zerar o estoque quando quantidade igual ao disponível")
    void decrementarEstoqueDeveZerarEstoque() {
        peca.decrementarEstoque(50);
        assertEquals(0, peca.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("incrementarEstoque deve aumentar o estoque corretamente")
    void incrementarEstoqueDeveAumentarCorretamente() {
        peca.incrementarEstoque(30);
        assertEquals(80, peca.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("getStatusEstoque retorna NORMAL quando estoque acima do mínimo")
    void statusEstoqueDeveSerNormal() {
        peca.setQuantidadeEstoque(50);
        peca.setEstoqueMinimo(10);
        assertEquals(StatusEstoque.NORMAL, peca.getStatusEstoque());
    }

    @Test
    @DisplayName("getStatusEstoque retorna LOW_STOCK quando estoque abaixo do mínimo")
    void statusEstoqueDeveSerLowStock() {
        peca.setQuantidadeEstoque(5);
        peca.setEstoqueMinimo(10);
        assertEquals(StatusEstoque.LOW_STOCK, peca.getStatusEstoque());
    }

    @Test
    @DisplayName("getStatusEstoque retorna OUT_OF_STOCK quando estoque é zero")
    void statusEstoqueDeveSerOutOfStock() {
        peca.setQuantidadeEstoque(0);
        assertEquals(StatusEstoque.OUT_OF_STOCK, peca.getStatusEstoque());
    }

    @Test
    @DisplayName("isEstoqueBaixo retorna true quando quantidadeEstoque < estoqueMinimo")
    void isEstoqueBaixoDeveRetornarTrue() {
        peca.setQuantidadeEstoque(5);
        peca.setEstoqueMinimo(10);
        assertTrue(peca.isEstoqueBaixo());
    }

    @Test
    @DisplayName("isEstoqueBaixo retorna false quando quantidadeEstoque >= estoqueMinimo")
    void isEstoqueBaixoDeveRetornarFalse() {
        peca.setQuantidadeEstoque(10);
        peca.setEstoqueMinimo(10);
        assertFalse(peca.isEstoqueBaixo());
    }

    @Test
    @DisplayName("desativar deve definir ativo como false")
    void desativarDeveSetarAtivoFalse() {
        peca.desativar();
        assertFalse(peca.getAtivo());
    }

    @Test
    @DisplayName("OSItemPeca.calcularSubtotal retorna quantidade × precoUnitarioNoMomento")
    void osItemPecaCalcularSubtotalDeveRetornarValorCorreto() {
        OSItemPeca item = new OSItemPeca();
        item.setPeca(peca);
        item.setQuantidade(3);
        item.setPrecoUnitarioNoMomento(new BigDecimal("45.90"));

        assertEquals(new BigDecimal("137.70"), item.calcularSubtotal());
    }
}
