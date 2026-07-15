package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoQuantidadeInput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
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
@DisplayName("AdicionarProdutoNaOrdemServicoUseCase - Testes Unitários")
class AdicionarProdutoNaOrdemServicoUseCaseTest {

    @Mock
    private OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    @Mock
    private ProdutoRepositoryPort produtoRepositoryPort;
    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private AdicionarProdutoNaOrdemServicoUseCase useCase;

    private Produto produtoAtivo;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        produtoAtivo = Produto.builder()
                .id(30L).codigo("P-30").nome("Filtro").ativo(true)
                .precoUnitario(BigDecimal.valueOf(50)).quantidadeEstoque(10).build();
        ordemServico = OrdemServico.builder()
                .id(1L).statusOS(StatusOS.EM_DIAGNOSTICO).valorTotal(BigDecimal.ZERO).build();
    }

    @Test
    @DisplayName("Deve adicionar produto, decrementar estoque e recalcular valor total")
    void deveAdicionarProdutoComSucesso() {
        OSItemProdutoQuantidadeInput input = new OSItemProdutoQuantidadeInput(30L, 2);

        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.of(produtoAtivo));
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(itemProdutoRepositoryPort.save(any(OSItemProduto.class))).thenAnswer(inv -> {
            OSItemProduto item = inv.getArgument(0);
            item.setId(500L);
            return item;
        });

        OSItemProdutoOutput output = useCase.executar(1L, input);

        assertNotNull(output);
        assertEquals(500L, output.id());
        assertEquals(2, output.quantidade());
        assertEquals(0, BigDecimal.valueOf(50).compareTo(output.precoUnitarioNoMomento()));
        assertEquals(8, produtoAtivo.getQuantidadeEstoque(), "estoque deve ser decrementado");
        assertEquals(0, BigDecimal.valueOf(100).compareTo(ordemServico.getValorTotal()));
        verify(produtoRepositoryPort).salvar(produtoAtivo);
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando o produto não existir")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        OSItemProdutoQuantidadeInput input = new OSItemProdutoQuantidadeInput(30L, 2);
        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> useCase.executar(1L, input));
        verify(itemProdutoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ProdutoInativoException quando o produto estiver inativo")
    void deveLancarExcecaoQuandoProdutoInativo() {
        Produto produtoInativo = Produto.builder()
                .id(30L).codigo("P-30").nome("Filtro").ativo(false)
                .precoUnitario(BigDecimal.valueOf(50)).quantidadeEstoque(10).build();
        OSItemProdutoQuantidadeInput input = new OSItemProdutoQuantidadeInput(30L, 2);
        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.of(produtoInativo));

        assertThrows(ProdutoInativoException.class, () -> useCase.executar(1L, input));
        verify(itemProdutoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando a OS não existir")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        OSItemProdutoQuantidadeInput input = new OSItemProdutoQuantidadeInput(30L, 2);
        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.of(produtoAtivo));
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(1L, input));
        verify(itemProdutoRepositoryPort, never()).save(any());
    }
}
