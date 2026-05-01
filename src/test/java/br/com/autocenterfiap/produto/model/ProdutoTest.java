package br.com.autocenterfiap.produto.model;

import br.com.autocenterfiap.ordemservico.model.OSItemProduto;
import br.com.autocenterfiap.produto.enums.StatusEstoque;
import br.com.autocenterfiap.produto.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.exception.EstoqueInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Produto - Testes Unitários de Domínio")
class ProdutoTest {

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setCodigo("FO-001");
        produto.setNome("Filtro de Óleo");
        produto.setUnidadeMedida(UnidadeMedida.UNIT);
        produto.setPrecoUnitario(new BigDecimal("45.90"));
        produto.setQuantidadeEstoque(50);
        produto.setEstoqueMinimo(10);
        produto.setCategoria("Motor");
        produto.setAtivo(true);
    }

    @Test
    @DisplayName("decrementarEstoque deve reduzir o estoque corretamente")
    void decrementarEstoqueDeveReduzirCorretamente() {
        produto.decrementarEstoque(20);
        assertEquals(30, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("decrementarEstoque deve lançar EstoqueInsuficienteException quando quantidade > estoque")
    void decrementarEstoqueDeveLancarExcecaoQuandoInsuficiente() {
        assertThrows(EstoqueInsuficienteException.class, () -> produto.decrementarEstoque(51));
    }

    @Test
    @DisplayName("decrementarEstoque deve lançar EstoqueInsuficienteException quando estoque é zero")
    void decrementarEstoqueDeveLancarExcecaoQuandoEstoqueZero() {
        produto.setQuantidadeEstoque(0);
        assertThrows(EstoqueInsuficienteException.class, () -> produto.decrementarEstoque(1));
    }

    @Test
    @DisplayName("decrementarEstoque deve zerar o estoque quando quantidade igual ao disponível")
    void decrementarEstoqueDeveZerarEstoque() {
        produto.decrementarEstoque(50);
        assertEquals(0, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("incrementarEstoque deve aumentar o estoque corretamente")
    void incrementarEstoqueDeveAumentarCorretamente() {
        produto.incrementarEstoque(30);
        assertEquals(80, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("getStatusEstoque retorna NORMAL quando estoque acima do mínimo")
    void statusEstoqueDeveSerNormal() {
        produto.setQuantidadeEstoque(50);
        produto.setEstoqueMinimo(10);
        assertEquals(StatusEstoque.NORMAL, produto.getStatusEstoque());
    }

    @Test
    @DisplayName("getStatusEstoque retorna LOW_STOCK quando estoque abaixo do mínimo")
    void statusEstoqueDeveSerLowStock() {
        produto.setQuantidadeEstoque(5);
        produto.setEstoqueMinimo(10);
        assertEquals(StatusEstoque.LOW_STOCK, produto.getStatusEstoque());
    }

    @Test
    @DisplayName("getStatusEstoque retorna OUT_OF_STOCK quando estoque é zero")
    void statusEstoqueDeveSerOutOfStock() {
        produto.setQuantidadeEstoque(0);
        assertEquals(StatusEstoque.OUT_OF_STOCK, produto.getStatusEstoque());
    }

    @Test
    @DisplayName("isEstoqueBaixo retorna true quando quantidadeEstoque < estoqueMinimo")
    void isEstoqueBaixoDeveRetornarTrue() {
        produto.setQuantidadeEstoque(5);
        produto.setEstoqueMinimo(10);
        assertTrue(produto.isEstoqueBaixo());
    }

    @Test
    @DisplayName("isEstoqueBaixo retorna false quando quantidadeEstoque >= estoqueMinimo")
    void isEstoqueBaixoDeveRetornarFalse() {
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(10);
        assertFalse(produto.isEstoqueBaixo());
    }

    @Test
    @DisplayName("desativar deve definir ativo como false")
    void desativarDeveSetarAtivoFalse() {
        produto.desativar();
        assertFalse(produto.getAtivo());
    }

    @Test
    @DisplayName("OSItemProduto.calcularSubtotal retorna quantidade × precoUnitarioNoMomento")
    void osItemProdutoCalcularSubtotalDeveRetornarValorCorreto() {
        OSItemProduto item = new OSItemProduto();
        item.setProduto(produto);
        item.setQuantidade(3);
        item.setPrecoUnitarioNoMomento(new BigDecimal("45.90"));

        assertEquals(new BigDecimal("137.70"), item.calcularSubtotal());
    }
}
