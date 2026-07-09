package br.com.autocenterfiap.produto.application.usecase;

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
@DisplayName("DesativarProdutoUseCase - Testes Unitários")
class DesativarProdutoUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private DesativarProdutoUseCase desativarProdutoUseCase;

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
    @DisplayName("Deve desativar produto com sucesso")
    void deveDesativarProdutoComSucesso() {
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(produtoValido));
        when(repositoryPort.salvar(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        desativarProdutoUseCase.executar(1L);

        assertFalse(produtoValido.getAtivo());
        verify(repositoryPort, times(1)).buscarPorId(1L);
        verify(repositoryPort, times(1)).salvar(produtoValido);
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(repositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> desativarProdutoUseCase.executar(99L));
        verify(repositoryPort, never()).salvar(any());
    }
}
