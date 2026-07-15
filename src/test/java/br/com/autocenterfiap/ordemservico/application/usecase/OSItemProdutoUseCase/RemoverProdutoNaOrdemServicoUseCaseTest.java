package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoverProdutoNaOrdemServicoUseCase - Testes Unitários")
class RemoverProdutoNaOrdemServicoUseCaseTest {

    @Mock
    private OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    @Mock
    private ProdutoRepositoryPort produtoRepositoryPort;

    @InjectMocks
    private RemoverProdutoNaOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve devolver a quantidade ao estoque e remover o item")
    void deveRemoverProdutoEDevolverEstoque() {
        Produto produto = Produto.builder()
                .id(30L).codigo("P-30").nome("Filtro").ativo(true)
                .precoUnitario(BigDecimal.valueOf(50)).quantidadeEstoque(10).build();
        OSItemProduto item = OSItemProduto.builder()
                .id(500L).produto(produto).quantidade(3)
                .precoUnitarioNoMomento(BigDecimal.valueOf(50)).build();

        when(itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(1L, 30L)).thenReturn(Optional.of(item));

        useCase.executar(1L, 30L);

        assertEquals(13, produto.getQuantidadeEstoque(), "estoque devolvido");
        verify(produtoRepositoryPort).salvar(produto);
        verify(itemProdutoRepositoryPort).deleteById(500L);
    }

    @Test
    @DisplayName("Deve lançar OSItemProdutoNaoEncontradoException quando o item não existir")
    void deveLancarExcecaoQuandoItemNaoEncontrado() {
        when(itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(1L, 30L)).thenReturn(Optional.empty());

        assertThrows(OSItemProdutoNaoEncontradoException.class, () -> useCase.executar(1L, 30L));
        verify(itemProdutoRepositoryPort, never()).deleteById(any());
    }
}
