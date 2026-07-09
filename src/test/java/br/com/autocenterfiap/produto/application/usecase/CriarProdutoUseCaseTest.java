package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.CriarProdutoInput;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.domain.exception.CodigoJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarProdutoUseCase - Testes Unitários")
class CriarProdutoUseCaseTest {

    @Mock
    private ProdutoRepositoryPort repositoryPort;

    @InjectMocks
    private CriarProdutoUseCase criarProdutoUseCase;

    private CriarProdutoInput inputValido;

    @BeforeEach
    void setUp() {
        inputValido = CriarProdutoInput.builder()
                .nome("Filtro de Ar")
                .codigo("FA-001")
                .descricao("Filtro de ar esportivo")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(89.90))
                .quantidadeEstoque(20)
                .estoqueMinimo(5)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .build();
    }

    @Test
    @DisplayName("Deve criar produto com sucesso quando os dados forem válidos")
    void deveCriarProdutoComSucesso() {
        when(repositoryPort.existsByCodigo("FA-001")).thenReturn(false);
        when(repositoryPort.salvar(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProdutoOutput output = criarProdutoUseCase.executar(inputValido);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("FA-001", output.getCodigo());
        assertEquals("Filtro de Ar", output.getNome());
        assertTrue(output.getAtivo());
        verify(repositoryPort, times(1)).salvar(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar CodigoJaCadastradoException quando o código já existir")
    void deveLancarExcecaoQuandoCodigoJaExistir() {
        when(repositoryPort.existsByCodigo("FA-001")).thenReturn(true);

        assertThrows(CodigoJaCadastradoException.class, () -> criarProdutoUseCase.executar(inputValido));
        verify(repositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando dados obrigatórios forem inválidos")
    void deveLancarExcecaoQuandoDadosObrigatoriosForemInvalidos() {
        // Nome em branco
        CriarProdutoInput inputSemNome = CriarProdutoInput.builder()
                .nome("")
                .codigo("FA-001")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(10.0))
                .quantidadeEstoque(5)
                .estoqueMinimo(1)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .build();
        assertThrows(IllegalArgumentException.class, () -> criarProdutoUseCase.executar(inputSemNome));

        // Preço negativo
        CriarProdutoInput inputPrecoNegativo = CriarProdutoInput.builder()
                .nome("Filtro de Ar")
                .codigo("FA-001")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(-1.0))
                .quantidadeEstoque(5)
                .estoqueMinimo(1)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .build();
        assertThrows(IllegalArgumentException.class, () -> criarProdutoUseCase.executar(inputPrecoNegativo));
    }
}
