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
@DisplayName("ListarProdutosUseCase - Testes Unitários")
class ListarProdutosUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private ListarProdutosUseCase listarProdutosUseCase;

    private Produto produtoValido;

    @BeforeEach
    void setUp() {
        produtoValido = Produto.builder()
                .id(1L)
                .nome("Filtro de Ar")
                .codigo("FA-001")
                .descricao("Filtro de ar esportivo")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(89.90))
                .quantidadeEstoque(20)
                .estoqueMinimo(5)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve listar produtos filtrando por categoria e busca")
    void deveListarProdutosComSucesso() {
        when(repositoryPort.buscarComFiltros("Filtros", "Ar", true))
                .thenReturn(List.of(produtoValido));

        List<ProdutoOutput> outputs = listarProdutosUseCase.executar("Filtros", "Ar");

        assertNotNull(outputs);
        assertEquals(1, outputs.size());
        assertEquals("FA-001", outputs.get(0).getCodigo());
        verify(repositoryPort, times(1)).buscarComFiltros("Filtros", "Ar", true);
    }
}
