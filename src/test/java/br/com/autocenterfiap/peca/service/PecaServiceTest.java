package br.com.autocenterfiap.peca.service;

import br.com.autocenterfiap.peca.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.peca.dto.PecaRequestDTO;
import br.com.autocenterfiap.peca.dto.PecaResponseDTO;
import br.com.autocenterfiap.peca.enums.StatusEstoque;
import br.com.autocenterfiap.peca.enums.TipoPeca;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.peca.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.peca.exception.PecaNaoEncontradaException;
import br.com.autocenterfiap.peca.model.Peca;
import br.com.autocenterfiap.peca.repository.PecaRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PecaService - Testes Unitários")
class PecaServiceTest {

    @Mock
    private PecaRepository pecaRepository;

    @InjectMocks
    private PecaService pecaService;

    private Peca peca;
    private PecaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        peca = new Peca();
        peca.setId(1L);
        peca.setCodigo("FO-001");
        peca.setNome("Filtro de Óleo");
        peca.setDescricao("Filtro para motores 1.0 a 2.0");
        peca.setUnidadeMedida(UnidadeMedida.UNIT);
        peca.setPrecoUnitario(new BigDecimal("45.90"));
        peca.setQuantidadeEstoque(100);
        peca.setEstoqueMinimo(10);
        peca.setCategoria("Motor");
        peca.setTipo(TipoPeca.PECAS);
        peca.setAtivo(true);

        requestDTO = new PecaRequestDTO(
                "Filtro de Óleo",
                "FO-001",
                "Filtro para motores 1.0 a 2.0",
                UnidadeMedida.UNIT,
                new BigDecimal("45.90"),
                100,
                10,
                "Motor",
                TipoPeca.PECAS
        );
    }

    @Test
    @DisplayName("Deve criar peça com sucesso")
    void deveCriarPecaComSucesso() {
        when(pecaRepository.existsByCodigo("FO-001")).thenReturn(false);
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        PecaResponseDTO result = pecaService.criar(requestDTO);

        assertNotNull(result);
        assertEquals("FO-001", result.codigo());
        verify(pecaRepository, times(1)).save(any(Peca.class));
    }

    @Test
    @DisplayName("Deve lançar CodigoJaCadastradoException ao criar com código duplicado")
    void deveLancarExcecaoAoCriarComCodigoDuplicado() {
        when(pecaRepository.existsByCodigo("FO-001")).thenReturn(true);

        assertThrows(CodigoJaCadastradoException.class, () -> pecaService.criar(requestDTO));
        verify(pecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar peça por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));

        PecaResponseDTO result = pecaService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("FO-001", result.codigo());
    }

    @Test
    @DisplayName("Deve lançar PecaNaoEncontradaException ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(pecaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PecaNaoEncontradaException.class, () -> pecaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve atualizar peça com sucesso")
    void deveAtualizarPecaComSucesso() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));
        when(pecaRepository.existsByCodigoAndIdNot("FO-001", 1L)).thenReturn(false);
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        PecaResponseDTO result = pecaService.atualizar(1L, requestDTO);

        assertNotNull(result);
        verify(pecaRepository, times(1)).save(peca);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com código já usado por outra peça")
    void deveLancarExcecaoAoAtualizarComCodigoDuplicado() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));
        when(pecaRepository.existsByCodigoAndIdNot("FO-001", 1L)).thenReturn(true);

        assertThrows(CodigoJaCadastradoException.class, () -> pecaService.atualizar(1L, requestDTO));
        verify(pecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desativar peça com sucesso")
    void deveDesativarPecaComSucesso() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        pecaService.desativar(1L);

        assertFalse(peca.getAtivo());
        verify(pecaRepository, times(1)).save(peca);
    }

    @Test
    @DisplayName("Deve adicionar estoque com sucesso")
    void deveAdicionarEstoqueComSucesso() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(50, "Reposição mensal");
        PecaResponseDTO result = pecaService.adicionarEstoque(1L, dto);

        assertEquals(150, peca.getQuantidadeEstoque());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve remover estoque com sucesso")
    void deveRemoverEstoqueComSucesso() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));
        when(pecaRepository.save(any(Peca.class))).thenReturn(peca);

        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(30, "Saída manual");
        pecaService.removerEstoque(1L, dto);

        assertEquals(70, peca.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao remover mais do que o disponível")
    void deveLancarExcecaoAoRemoverMaisDoQueDisponivel() {
        when(pecaRepository.findById(1L)).thenReturn(Optional.of(peca));

        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(200, "Saída inválida");
        assertThrows(EstoqueInsuficienteException.class, () -> pecaService.removerEstoque(1L, dto));
    }

    @Test
    @DisplayName("listar deve retornar lista filtrada")
    void deveListarComFiltros() {
        when(pecaRepository.buscarComFiltros(anyString(), any(), any(Boolean.class)))
                .thenReturn(List.of(peca));

        List<PecaResponseDTO> result = pecaService.listar("Motor", null);

        assertEquals(1, result.size());
        assertEquals("FO-001", result.get(0).codigo());
    }

    @Test
    @DisplayName("listarAlertasEstoque deve retornar peças com estoque crítico")
    void deveListarAlertasEstoque() {
        Peca pecaBaixo = new Peca();
        pecaBaixo.setId(2L);
        pecaBaixo.setCodigo("OL-001");
        pecaBaixo.setNome("Óleo de Motor");
        pecaBaixo.setUnidadeMedida(UnidadeMedida.LITER);
        pecaBaixo.setPrecoUnitario(new BigDecimal("25.00"));
        pecaBaixo.setQuantidadeEstoque(3);
        pecaBaixo.setEstoqueMinimo(10);
        pecaBaixo.setCategoria("Fluidos");
        pecaBaixo.setTipo(TipoPeca.INSUMOS);
        pecaBaixo.setAtivo(true);

        when(pecaRepository.findPecasSemEstoque()).thenReturn(List.of());
        when(pecaRepository.findPecasComEstoqueBaixo()).thenReturn(List.of(pecaBaixo));

        List<PecaResponseDTO> alertas = pecaService.listarPecasComProblemaDeEstoque();

        assertEquals(1, alertas.size());
        assertEquals(StatusEstoque.LOW_STOCK, alertas.get(0).statusEstoque());
    }
}
