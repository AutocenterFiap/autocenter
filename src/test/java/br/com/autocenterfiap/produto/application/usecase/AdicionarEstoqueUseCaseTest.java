package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.MovimentacaoEstoqueInput;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdicionarEstoqueUseCase - Testes Unitários")
class AdicionarEstoqueUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private AdicionarEstoqueUseCase adicionarEstoqueUseCase;

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
    @DisplayName("Deve adicionar estoque com sucesso")
    void deveAdicionarEstoqueComSucesso() {
        MovimentacaoEstoqueInput input = new MovimentacaoEstoqueInput(10);
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(produtoValido));
        when(repositoryPort.salvar(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoOutput output = adicionarEstoqueUseCase.executar(1L, input);

        assertNotNull(output);
        assertEquals(30, output.getQuantidadeEstoque());
        verify(repositoryPort, times(1)).buscarPorId(1L);
        verify(repositoryPort, times(1)).salvar(produtoValido);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para quantidade de incremento inválida")
    void deveLancarExcecaoParaQuantidadeInvalida() {
        MovimentacaoEstoqueInput input = new MovimentacaoEstoqueInput(-5);
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(produtoValido));

        assertThrows(IllegalArgumentException.class, () -> adicionarEstoqueUseCase.executar(1L, input));
        verify(repositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        MovimentacaoEstoqueInput input = new MovimentacaoEstoqueInput(10);
        when(repositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> adicionarEstoqueUseCase.executar(99L, input));
        verify(repositoryPort, never()).salvar(any());
    }
}
