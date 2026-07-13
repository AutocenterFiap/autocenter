package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemProdutoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemProdutoJpaRepository;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoRequestDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.OSItemProdutoResponseDTO;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.domain.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.repository.ProdutoJpaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OSItemProdutoService - Testes Unitários")
class OSItemProdutoJpaEntityServiceTest {

    @Mock
    private OSItemProdutoJpaRepository osItemProdutoJpaRepository;

    @Mock
    private ProdutoJpaRepository produtoJpaRepository;

    @Mock
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    @InjectMocks
    private OSItemProdutoService osItemProdutoService;

    private ProdutoJpaEntity produto;
    private OrdemServicoJpaEntity os;
    private OSItemProdutoJpaEntity osItem;

    @BeforeEach
    void setUp() {
        produto = new ProdutoJpaEntity();
        produto.setId(1L);
        produto.setCodigo("FO-001");
        produto.setNome("Filtro de Óleo");
        produto.setUnidadeMedida(UnidadeMedida.UNIT);
        produto.setPrecoUnitario(new BigDecimal("45.90"));
        produto.setQuantidadeEstoque(50);
        produto.setEstoqueMinimo(10);
        produto.setCategoria("Motor");
        produto.setAtivo(true);

        os = new OrdemServicoJpaEntity();
        os.setId(10L);

        osItem = new OSItemProdutoJpaEntity();
        osItem.setId(1L);
        osItem.setOrdemServicoJpaEntity(os);
        osItem.setProduto(produto);
        osItem.setQuantidade(2);
    }

    @Test
    @DisplayName("Deve adicionar produto na OS e decrementar estoque")
    void deveAdicionarProdutoNaOS() {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 3);

        when(produtoJpaRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(osItemProdutoJpaRepository.save(any(OSItemProdutoJpaEntity.class))).thenReturn(osItem);
        when(ordemServicoJpaRepository.getReferenceById(10L)).thenReturn(os);

        OSItemProdutoResponseDTO result = osItemProdutoService.adicionarProdutoNaOS(10L, dto);

        assertNotNull(result);
        assertEquals(47, produto.getQuantidadeEstoque()); // 50 - 3 = 47
        verify(osItemProdutoJpaRepository, times(1)).save(any(OSItemProdutoJpaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar ProdutoInativoException ao adicionar produto inativo à OS")
    void deveLancarExcecaoAoAdicionarProdutoInativo() {
        produto.setAtivo(false);
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 2);

        when(produtoJpaRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(ProdutoInativoException.class, () -> osItemProdutoService.adicionarProdutoNaOS(10L, dto));
        verify(osItemProdutoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao adicionar produto sem estoque")
    void deveLancarExcecaoAoAdicionarProdutoSemEstoque() {
        produto.setQuantidadeEstoque(1);
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 5);

        when(produtoJpaRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(EstoqueInsuficienteException.class, () -> osItemProdutoService.adicionarProdutoNaOS(10L, dto));
    }

    @Test
    @DisplayName("Deve remover produto da OS e devolver ao estoque")
    void deveRemoverProdutoDaOSEDevolverEstoque() {
        os.getOsItensProdutos().add(osItem);
        
        when(osItemProdutoJpaRepository.findByOrdemServicoJpaEntityIdAndProdutoId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemProdutoService.removerProdutoDaOS(10L, 1L);

        // Valida se o estoque voltou e se o item sumiu da lista da OS
        assertEquals(52, produto.getQuantidadeEstoque()); // 50 + 2 = 52
        assertEquals(0, os.getOsItensProdutos().size(), "O item deveria ter sido removido da lista");
    }

    @Test
    @DisplayName("Deve lançar OSItemProdutoNaoEncontradoException ao remover produto não vinculado")
    void deveLancarExcecaoAoRemoverItemInexistente() {
        when(osItemProdutoJpaRepository.findByOrdemServicoJpaEntityIdAndProdutoId(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(OSItemProdutoNaoEncontradoException.class,
                () -> osItemProdutoService.removerProdutoDaOS(10L, 99L));
    }

    @Test
    @DisplayName("Deve listar produtos de uma OS")
    void deveListarProdutosDaOS() {
        when(osItemProdutoJpaRepository.findByOrdemServicoJpaEntityId(10L, )).thenReturn(List.of(osItem));

        List<OSItemProdutoResponseDTO> result = osItemProdutoService.listarPorOS(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).ordemServicoId());
    }

    @Test
    @DisplayName("Deve aumentar quantidade e decrementar estoque adicionalmente")
    void deveAtualizarQuantidadeAumentandoEstoque() {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 5); // era 2, agora 5

        when(osItemProdutoJpaRepository.findByOrdemServicoJpaEntityIdAndProdutoId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemProdutoService.atualizarQuantidade(10L, 1L, dto);

        assertEquals(47, produto.getQuantidadeEstoque()); // 50 - (5-2) = 47
    }

    @Test
    @DisplayName("Deve reduzir quantidade e devolver diferença ao estoque")
    void deveAtualizarQuantidadeReduzindoEstoque() {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(1L, 1); // era 2, agora 1

        when(osItemProdutoJpaRepository.findByOrdemServicoJpaEntityIdAndProdutoId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemProdutoService.atualizarQuantidade(10L, 1L, dto);

        assertEquals(51, produto.getQuantidadeEstoque()); // 50 + (2-1) = 51
    }
}
