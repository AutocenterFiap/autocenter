package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.repository.OSItemProdutoRepository;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.ordemservico.model.OSItemProduto;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.produto.dto.OSItemProdutoRequestDTO;
import br.com.autocenterfiap.produto.dto.OSItemProdutoResponseDTO;
import br.com.autocenterfiap.produto.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.produto.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.model.Produto;
import br.com.autocenterfiap.produto.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OSItemProdutoService - Testes Unitários")
class OSItemProdutoServiceTest {

    @Mock
    private OSItemProdutoRepository osItemProdutoRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private OSItemProdutoService osItemProdutoService;

    private Produto produto;
    private OrdemServico os;
    private OSItemProduto osItem;

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

        os = new OrdemServico();
        os.setId(10L);

        osItem = new OSItemProduto();
        osItem.setId(1L);
        osItem.setOrdemServico(os);
        osItem.setProduto(produto);
        osItem.setQuantidade(2);
    }

    @Test
    @DisplayName("Deve adicionar produto na OS e decrementar estoque")
    void deveAdicionarProdutoNaOS() {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 3);

        when(produtoService.buscarOuLancarExcecao(1L)).thenReturn(produto);
        when(osItemProdutoRepository.save(any(OSItemProduto.class))).thenReturn(osItem);
        when(ordemServicoRepository.getReferenceById(10L)).thenReturn(os);

        OSItemProdutoResponseDTO result = osItemProdutoService.adicionarProdutoNaOS(10L, dto);

        assertNotNull(result);
        assertEquals(47, produto.getQuantidadeEstoque()); // 50 - 3 = 47
        verify(osItemProdutoRepository, times(1)).save(any(OSItemProduto.class));
    }

    @Test
    @DisplayName("Deve lançar ProdutoInativoException ao adicionar produto inativo à OS")
    void deveLancarExcecaoAoAdicionarProdutoInativo() {
        produto.setAtivo(false);
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 2);

        when(produtoService.buscarOuLancarExcecao(1L)).thenReturn(produto);

        assertThrows(ProdutoInativoException.class, () -> osItemProdutoService.adicionarProdutoNaOS(10L, dto));
        verify(osItemProdutoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao adicionar produto sem estoque")
    void deveLancarExcecaoAoAdicionarProdutoSemEstoque() {
        produto.setQuantidadeEstoque(1);
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 5);

        when(produtoService.buscarOuLancarExcecao(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class, () -> osItemProdutoService.adicionarProdutoNaOS(10L, dto));
    }

    @Test
    @DisplayName("Deve remover produto da OS e devolver ao estoque")
    void deveRemoverProdutoDaOSEDevolverEstoque() {
        os.getOsItensProdutos().add(osItem);
        
        when(osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemProdutoService.removerProdutoDaOS(10L, 1L);

        // Valida se o estoque voltou e se o item sumiu da lista da OS
        assertEquals(52, produto.getQuantidadeEstoque()); // 50 + 2 = 52
        assertEquals(0, os.getOsItensProdutos().size(), "O item deveria ter sido removido da lista");
    }

    @Test
    @DisplayName("Deve lançar OSItemProdutoNaoEncontradoException ao remover produto não vinculado")
    void deveLancarExcecaoAoRemoverItemInexistente() {
        when(osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(OSItemProdutoNaoEncontradoException.class,
                () -> osItemProdutoService.removerProdutoDaOS(10L, 99L));
    }

    @Test
    @DisplayName("Deve listar produtos de uma OS")
    void deveListarProdutosDaOS() {
        when(osItemProdutoRepository.findByOrdemServicoId(10L)).thenReturn(List.of(osItem));

        List<OSItemProdutoResponseDTO> result = osItemProdutoService.listarPorOS(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).ordemServicoId());
    }

    @Test
    @DisplayName("Deve aumentar quantidade e decrementar estoque adicionalmente")
    void deveAtualizarQuantidadeAumentandoEstoque() {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 5); // era 2, agora 5

        when(osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemProdutoService.atualizarQuantidade(10L, 1L, dto);

        assertEquals(47, produto.getQuantidadeEstoque()); // 50 - (5-2) = 47
    }

    @Test
    @DisplayName("Deve reduzir quantidade e devolver diferença ao estoque")
    void deveAtualizarQuantidadeReduzindoEstoque() {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 1); // era 2, agora 1

        when(osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemProdutoService.atualizarQuantidade(10L, 1L, dto);

        assertEquals(51, produto.getQuantidadeEstoque()); // 50 + (2-1) = 51
    }
}
