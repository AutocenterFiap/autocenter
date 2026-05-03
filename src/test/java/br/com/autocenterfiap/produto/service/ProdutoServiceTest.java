package br.com.autocenterfiap.produto.service;

import br.com.autocenterfiap.produto.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.dto.ProdutoResponseDTO;
import br.com.autocenterfiap.produto.enums.StatusEstoque;
import br.com.autocenterfiap.produto.enums.TipoProduto;
import br.com.autocenterfiap.produto.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.produto.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.produto.exception.ProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.model.Produto;
import br.com.autocenterfiap.produto.repository.ProdutoRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService - Testes Unitários")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setCodigo("FO-001");
        produto.setNome("Filtro de Óleo");
        produto.setDescricao("Filtro para motores 1.0 a 2.0");
        produto.setUnidadeMedida(UnidadeMedida.UNIT);
        produto.setPrecoUnitario(new BigDecimal("45.90"));
        produto.setQuantidadeEstoque(100);
        produto.setEstoqueMinimo(10);
        produto.setCategoria("Motor");
        produto.setTipo(TipoProduto.PECAS);
        produto.setAtivo(true);

        requestDTO = new ProdutoRequestDTO(
                "Filtro de Óleo",
                "FO-001",
                "Filtro para motores 1.0 a 2.0",
                UnidadeMedida.UNIT,
                new BigDecimal("45.90"),
                100,
                10,
                "Motor",
                TipoProduto.PECAS
        );
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        when(produtoRepository.existsByCodigo("FO-001")).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO result = produtoService.criar(requestDTO);

        assertNotNull(result);
        assertEquals("FO-001", result.codigo());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar CodigoJaCadastradoException ao criar com código duplicado")
    void deveLancarExcecaoAoCriarComCodigoDuplicado() {
        when(produtoRepository.existsByCodigo("FO-001")).thenReturn(true);

        assertThrows(CodigoJaCadastradoException.class, () -> produtoService.criar(requestDTO));
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponseDTO result = produtoService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("FO-001", result.codigo());
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.existsByCodigoAndIdNot("FO-001", 1L)).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO result = produtoService.atualizar(1L, requestDTO);

        assertNotNull(result);
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com código já usado por outro produto")
    void deveLancarExcecaoAoAtualizarComCodigoDuplicado() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.existsByCodigoAndIdNot("FO-001", 1L)).thenReturn(true);

        assertThrows(CodigoJaCadastradoException.class, () -> produtoService.atualizar(1L, requestDTO));
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desativar produto com sucesso")
    void deveDesativarProdutoComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        produtoService.desativar(1L);

        assertFalse(produto.getAtivo());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve adicionar estoque com sucesso")
    void deveAdicionarEstoqueComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(50, "Reposição mensal");
        ProdutoResponseDTO result = produtoService.adicionarEstoque(1L, dto);

        assertEquals(150, produto.getQuantidadeEstoque());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve remover estoque com sucesso")
    void deveRemoverEstoqueComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(30, "Saída manual");
        produtoService.removerEstoque(1L, dto);

        assertEquals(70, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao remover mais do que o disponível")
    void deveLancarExcecaoAoRemoverMaisDoQueDisponivel() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(200, "Saída inválida");
        assertThrows(EstoqueInsuficienteException.class, () -> produtoService.removerEstoque(1L, dto));
    }

    @Test
    @DisplayName("listar deve retornar lista filtrada")
    void deveListarComFiltros() {
        when(produtoRepository.buscarComFiltros(anyString(), any(), any(Boolean.class)))
                .thenReturn(List.of(produto));

        List<ProdutoResponseDTO> result = produtoService.listar("Motor", null);

        assertEquals(1, result.size());
        assertEquals("FO-001", result.get(0).codigo());
    }

    @Test
    @DisplayName("listarAlertasEstoque deve retornar produtos com estoque crítico")
    void deveListarAlertasEstoque() {
        Produto produtoBaixo = new Produto();
        produtoBaixo.setId(2L);
        produtoBaixo.setCodigo("OL-001");
        produtoBaixo.setNome("Óleo de Motor");
        produtoBaixo.setUnidadeMedida(UnidadeMedida.LITER);
        produtoBaixo.setPrecoUnitario(new BigDecimal("25.00"));
        produtoBaixo.setQuantidadeEstoque(3);
        produtoBaixo.setEstoqueMinimo(10);
        produtoBaixo.setCategoria("Fluidos");
        produtoBaixo.setTipo(TipoProduto.INSUMOS);
        produtoBaixo.setAtivo(true);

        when(produtoRepository.findProdutosSemEstoque()).thenReturn(List.of());
        when(produtoRepository.findProdutosComEstoqueBaixo()).thenReturn(List.of(produtoBaixo));

        List<ProdutoResponseDTO> alertas = produtoService.listarProdutosComProblemaDeEstoque();

        assertEquals(1, alertas.size());
        assertEquals(StatusEstoque.LOW_STOCK, alertas.get(0).statusEstoque());
    }
}
