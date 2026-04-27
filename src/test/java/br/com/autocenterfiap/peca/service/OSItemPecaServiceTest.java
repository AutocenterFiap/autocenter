package br.com.autocenterfiap.peca.service;

import br.com.autocenterfiap.peca.dto.OSItemPecaRequestDTO;
import br.com.autocenterfiap.peca.dto.OSItemPecaResponseDTO;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.peca.exception.OSItemPecaNaoEncontradoException;
import br.com.autocenterfiap.peca.exception.PecaInativaException;
import br.com.autocenterfiap.peca.model.OSItemPeca;
import br.com.autocenterfiap.peca.model.Peca;
import br.com.autocenterfiap.peca.repository.OSItemPecaRepository;
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
@DisplayName("OSItemPecaService - Testes Unitários")
class OSItemPecaServiceTest {

    @Mock
    private OSItemPecaRepository osItemPecaRepository;

    @Mock
    private PecaService pecaService;

    @InjectMocks
    private OSItemPecaService osItemPecaService;

    private Peca peca;
    private OSItemPeca osItem;

    @BeforeEach
    void setUp() {
        peca = new Peca();
        peca.setId(1L);
        peca.setCodigo("FO-001");
        peca.setNome("Filtro de Óleo");
        peca.setUnidadeMedida(UnidadeMedida.UNIT);
        peca.setPrecoUnitario(new BigDecimal("45.90"));
        peca.setQuantidadeEstoque(50);
        peca.setEstoqueMinimo(10);
        peca.setCategoria("Motor");
        peca.setAtivo(true);

        osItem = new OSItemPeca();
        osItem.setId(1L);
        osItem.setOrdemServicoId(10L);
        osItem.setPeca(peca);
        osItem.setQuantidade(2);
        osItem.setPrecoUnitarioNoMomento(new BigDecimal("45.90"));
    }

    @Test
    @DisplayName("Deve adicionar peça na OS e decrementar estoque")
    void deveAdicionarPecaNaOS() {
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(1L, 3);

        when(pecaService.buscarOuLancarExcecao(1L)).thenReturn(peca);
        when(osItemPecaRepository.save(any(OSItemPeca.class))).thenReturn(osItem);

        OSItemPecaResponseDTO result = osItemPecaService.adicionarPecaNaOS(10L, dto);

        assertNotNull(result);
        assertEquals(47, peca.getQuantidadeEstoque()); // 50 - 3 = 47
        verify(osItemPecaRepository, times(1)).save(any(OSItemPeca.class));
    }

    @Test
    @DisplayName("Deve lançar PecaInativaException ao adicionar peça inativa à OS")
    void deveLancarExcecaoAoAdicionarPecaInativa() {
        peca.setAtivo(false);
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(1L, 2);

        when(pecaService.buscarOuLancarExcecao(1L)).thenReturn(peca);

        assertThrows(PecaInativaException.class, () -> osItemPecaService.adicionarPecaNaOS(10L, dto));
        verify(osItemPecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao adicionar peça sem estoque")
    void deveLancarExcecaoAoAdicionarPecaSemEstoque() {
        peca.setQuantidadeEstoque(1);
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(1L, 5);

        when(pecaService.buscarOuLancarExcecao(1L)).thenReturn(peca);

        assertThrows(EstoqueInsuficienteException.class, () -> osItemPecaService.adicionarPecaNaOS(10L, dto));
    }

    @Test
    @DisplayName("Deve remover peça da OS e devolver ao estoque")
    void deveRemoverPecaDaOSEDevolverEstoque() {
        when(osItemPecaRepository.findByOrdemServicoIdAndPecaId(10L, 1L))
                .thenReturn(Optional.of(osItem));

        osItemPecaService.removerPecaDaOS(10L, 1L);

        assertEquals(52, peca.getQuantidadeEstoque()); // 50 + 2 = 52
        verify(osItemPecaRepository, times(1)).delete(osItem);
    }

    @Test
    @DisplayName("Deve lançar OSItemPecaNaoEncontradoException ao remover peça não vinculada")
    void deveLancarExcecaoAoRemoverItemInexistente() {
        when(osItemPecaRepository.findByOrdemServicoIdAndPecaId(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(OSItemPecaNaoEncontradoException.class,
                () -> osItemPecaService.removerPecaDaOS(10L, 99L));
    }

    @Test
    @DisplayName("Deve listar peças de uma OS")
    void deveListarPecasDaOS() {
        when(osItemPecaRepository.findByOrdemServicoId(10L)).thenReturn(List.of(osItem));

        List<OSItemPecaResponseDTO> result = osItemPecaService.listarPorOS(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).ordemServicoId());
    }

    @Test
    @DisplayName("Deve aumentar quantidade e decrementar estoque adicionalmente")
    void deveAtualizarQuantidadeAumentandoEstoque() {
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(1L, 5); // era 2, agora 5

        when(osItemPecaRepository.findByOrdemServicoIdAndPecaId(10L, 1L))
                .thenReturn(Optional.of(osItem));
        when(osItemPecaRepository.save(any(OSItemPeca.class))).thenReturn(osItem);

        osItemPecaService.atualizarQuantidade(10L, 1L, dto);

        assertEquals(47, peca.getQuantidadeEstoque()); // 50 - (5-2) = 47
    }

    @Test
    @DisplayName("Deve reduzir quantidade e devolver diferença ao estoque")
    void deveAtualizarQuantidadeReduzindoEstoque() {
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(1L, 1); // era 2, agora 1

        when(osItemPecaRepository.findByOrdemServicoIdAndPecaId(10L, 1L))
                .thenReturn(Optional.of(osItem));
        when(osItemPecaRepository.save(any(OSItemPeca.class))).thenReturn(osItem);

        osItemPecaService.atualizarQuantidade(10L, 1L, dto);

        assertEquals(51, peca.getQuantidadeEstoque()); // 50 + (2-1) = 51
    }
}
