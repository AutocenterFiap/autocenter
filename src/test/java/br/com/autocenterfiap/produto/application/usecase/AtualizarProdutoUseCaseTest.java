package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.AtualizarProdutoInput;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.domain.exception.CodigoJaCadastradoException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarProdutoUseCase - Testes Unitários")
class AtualizarProdutoUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private AtualizarProdutoUseCase atualizarProdutoUseCase;

    private Produto produtoValido;
    private AtualizarProdutoInput inputValido;

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

        inputValido = AtualizarProdutoInput.builder()
                .nome("Filtro de Ar Premium")
                .codigo("FA-002")
                .descricao("Filtro de ar de alta performance")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(99.90))
                .quantidadeEstoque(15)
                .estoqueMinimo(5)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .build();
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso quando os dados forem válidos")
    void deveAtualizarProdutoComSucesso() {
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(produtoValido));
        when(repositoryPort.existsByCodigoAndIdNot("FA-002", 1L)).thenReturn(false);
        when(repositoryPort.salvar(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoOutput output = atualizarProdutoUseCase.executar(1L, inputValido);

        assertNotNull(output);
        assertEquals("FA-002", output.getCodigo());
        assertEquals("Filtro de Ar Premium", output.getNome());
        verify(repositoryPort, times(1)).buscarPorId(1L);
        verify(repositoryPort, times(1)).salvar(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando ID não for encontrado")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {
        when(repositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> atualizarProdutoUseCase.executar(99L, inputValido));
        verify(repositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar CodigoJaCadastradoException quando código já existir em outro produto")
    void deveLancarExcecaoQuandoCodigoJaExistirEmOutroProduto() {
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(produtoValido));
        when(repositoryPort.existsByCodigoAndIdNot("FA-002", 1L)).thenReturn(true);

        assertThrows(CodigoJaCadastradoException.class, () -> atualizarProdutoUseCase.executar(1L, inputValido));
        verify(repositoryPort, never()).salvar(any());
    }
}
