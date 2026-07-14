package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarProdutoPorIdUseCase - Testes Unitários")
class BuscarProdutoPorIdUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase;

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
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() {
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(produtoValido));

        ProdutoOutput output = buscarProdutoPorIdUseCase.executar(1L);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("FA-001", output.getCodigo());
        verify(repositoryPort, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando o ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(repositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> buscarProdutoPorIdUseCase.executar(99L));
        verify(repositoryPort, times(1)).buscarPorId(99L);
    }
}
