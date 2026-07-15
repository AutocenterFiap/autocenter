package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoQuantidadeInput;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
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
@DisplayName("AtualizarQuantidadeUseCase - Testes Unitários")
class AtualizarQuantidadeUseCaseTest {

    @Mock
    private OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    @Mock
    private ProdutoRepositoryPort produtoRepositoryPort;

    @InjectMocks
    private AtualizarQuantidadeUseCase useCase;

    private Produto produto;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(30L).codigo("P-30").nome("Filtro").ativo(true)
                .precoUnitario(BigDecimal.valueOf(50)).quantidadeEstoque(10).build();
        ordemServico = OrdemServico.builder()
                .id(1L).statusOS(StatusOS.EM_DIAGNOSTICO).valorTotal(BigDecimal.ZERO).build();
    }

    private OSItemProduto itemComQuantidade(int quantidade) {
        return OSItemProduto.builder()
                .id(500L)
                .ordemServico(ordemServico)
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitarioNoMomento(BigDecimal.valueOf(50))
                .build();
    }

    @Test
    @DisplayName("Deve decrementar estoque ao aumentar a quantidade do item")
    void deveAumentarQuantidadeEDecrementarEstoque() {
        OSItemProduto item = itemComQuantidade(2);
        when(itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(1L, 30L)).thenReturn(Optional.of(item));
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(itemProdutoRepositoryPort.save(any(OSItemProduto.class))).thenAnswer(inv -> inv.getArgument(0));

        OSItemProdutoOutput output = useCase.executar(1L, 30L, new OSItemProdutoQuantidadeInput(30L, 5));

        assertEquals(5, output.quantidade());
        assertEquals(7, produto.getQuantidadeEstoque(), "estoque decrementado em 3");
        verify(produtoRepositoryPort).salvar(produto);
    }

    @Test
    @DisplayName("Deve incrementar estoque ao reduzir a quantidade do item")
    void deveReduzirQuantidadeEIncrementarEstoque() {
        OSItemProduto item = itemComQuantidade(5);
        when(itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(1L, 30L)).thenReturn(Optional.of(item));
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(itemProdutoRepositoryPort.save(any(OSItemProduto.class))).thenAnswer(inv -> inv.getArgument(0));

        OSItemProdutoOutput output = useCase.executar(1L, 30L, new OSItemProdutoQuantidadeInput(30L, 2));

        assertEquals(2, output.quantidade());
        assertEquals(13, produto.getQuantidadeEstoque(), "estoque incrementado em 3");
        verify(produtoRepositoryPort).salvar(produto);
    }

    @Test
    @DisplayName("Deve lançar OSItemProdutoNaoEncontradoException quando o item não existir")
    void deveLancarExcecaoQuandoItemNaoEncontrado() {
        when(itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(1L, 30L)).thenReturn(Optional.empty());

        assertThrows(OSItemProdutoNaoEncontradoException.class,
                () -> useCase.executar(1L, 30L, new OSItemProdutoQuantidadeInput(30L, 5)));
        verify(itemProdutoRepositoryPort, never()).save(any());
    }
}
