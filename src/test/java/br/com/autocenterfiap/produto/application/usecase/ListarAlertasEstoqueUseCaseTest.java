package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarAlertasEstoqueUseCase - Testes Unitários")
class ListarAlertasEstoqueUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private ListarAlertasEstoqueUseCase listarAlertasEstoqueUseCase;

    private Produto produtoSemEstoque;
    private Produto produtoEstoqueBaixo;

    @BeforeEach
    void setUp() {
        produtoSemEstoque = Produto.builder()
                .id(1L)
                .nome("Filtro de Ar")
                .codigo("FA-001")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(89.90))
                .quantidadeEstoque(0)
                .estoqueMinimo(5)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .ativo(true)
                .build();

        produtoEstoqueBaixo = Produto.builder()
                .id(2L)
                .nome("Filtro de Óleo")
                .codigo("FO-001")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(45.90))
                .quantidadeEstoque(3)
                .estoqueMinimo(10)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve listar alertas de estoque combinando produtos sem estoque e com estoque baixo de forma distinta")
    void deveListarAlertasDeEstoque() {
        // Mocking both repository queries. Let's make one overlapping product to test distinct
        when(repositoryPort.findProdutosSemEstoque()).thenReturn(List.of(produtoSemEstoque));
        when(repositoryPort.findProdutosComEstoqueBaixo()).thenReturn(List.of(produtoEstoqueBaixo, produtoSemEstoque));

        List<ProdutoOutput> outputs = listarAlertasEstoqueUseCase.executar();

        assertNotNull(outputs);
        assertEquals(2, outputs.size());
        
        // Outputs should contain FA-001 and FO-001 exactly once.
        long countFA = outputs.stream().filter(o -> o.getCodigo().equals("FA-001")).count();
        long countFO = outputs.stream().filter(o -> o.getCodigo().equals("FO-001")).count();
        
        assertEquals(1, countFA);
        assertEquals(1, countFO);
        
        verify(repositoryPort, times(1)).findProdutosSemEstoque();
        verify(repositoryPort, times(1)).findProdutosComEstoqueBaixo();
    }
}
