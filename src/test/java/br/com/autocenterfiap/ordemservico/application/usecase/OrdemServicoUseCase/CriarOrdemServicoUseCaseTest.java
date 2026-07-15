package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.validator.OrdemServicoValidator;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarOrdemServicoUseCase - Testes Unitários")
class CriarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    @Mock
    private OSItemProdutoRepositoryPort osItemProdutoRepositoryPort;
    @Mock
    private OSItemServicoRepositoryPort osItemServicoRepositoryPort;
    @Mock
    private ProdutoRepositoryPort produtoRepositoryPort;
    @Mock
    private ServicoRepositoryPort servicoRepositoryPort;
    @Mock
    private VeiculoRepositoryPort veiculoRepositoryPort;
    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;
    @Mock
    private OrdemServicoValidator validator;

    private CriarOrdemServicoUseCase useCase;

    private Veiculo veiculo;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        useCase = new CriarOrdemServicoUseCase(
                ordemServicoRepositoryPort,
                osItemProdutoRepositoryPort,
                osItemServicoRepositoryPort,
                produtoRepositoryPort,
                servicoRepositoryPort,
                veiculoRepositoryPort,
                clienteRepositoryPort,
                List.of(validator));

        veiculo = Veiculo.builder().id(1L).placa("ABC1D23").build();
        cliente = Cliente.builder().id(2L).nome("João").build();
    }

    private OrdemServico ordemServicoSalva() {
        return OrdemServico.builder()
                .id(100L)
                .statusOS(StatusOS.ABERTA)
                .valorTotal(BigDecimal.ZERO)
                .veiculo(veiculo)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso sem itens")
    void deveCriarOrdemServicoComSucesso() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, Collections.emptyList(), Collections.emptyMap());

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(clienteRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepositoryPort.save(any(OrdemServico.class))).thenReturn(ordemServicoSalva());

        OrdemServicoOutput output = useCase.executar(input);

        assertNotNull(output);
        assertEquals(100L, output.id());
        assertEquals(StatusOS.ABERTA, output.statusOS());
        assertEquals(1L, output.veiculoId());
        assertEquals(2L, output.clienteId());
        verify(validator).validate(input);
        verify(ordemServicoRepositoryPort, times(2)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve adicionar produtos e serviços informados durante a criação")
    void deveAdicionarProdutosEServicosNaCriacao() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, List.of(50L), Map.of(30L, 2));

        Produto produto = Produto.builder()
                .id(30L).codigo("P-30").nome("Peça").ativo(true)
                .precoUnitario(BigDecimal.valueOf(10)).quantidadeEstoque(10).build();
        Servico servico = Servico.builder()
                .id(50L).descricao("Troca").status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100)).build();

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(clienteRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepositoryPort.save(any(OrdemServico.class))).thenReturn(ordemServicoSalva());
        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.of(produto));
        when(servicoRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(servico));
        when(osItemProdutoRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(osItemServicoRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput output = useCase.executar(input);

        assertNotNull(output);
        assertEquals(8, produto.getQuantidadeEstoque(), "estoque deve ser decrementado");
        verify(produtoRepositoryPort).salvar(produto);
        verify(osItemProdutoRepositoryPort).save(any());
        verify(osItemServicoRepositoryPort).save(any());
    }

    @Test
    @DisplayName("Deve lançar VeiculoNaoEncontradoException quando veículo não existir")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, Collections.emptyList(), Collections.emptyMap());

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(VeiculoNaoEncontradoException.class, () -> useCase.executar(input));
        verify(ordemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ClienteNaoEncontradoException quando cliente não existir")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, Collections.emptyList(), Collections.emptyMap());

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(clienteRepositoryPort.buscarPorId(2L)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class, () -> useCase.executar(input));
        verify(ordemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando produto informado não existir")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, Collections.emptyList(), Map.of(30L, 1));

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(clienteRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepositoryPort.save(any(OrdemServico.class))).thenReturn(ordemServicoSalva());
        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> useCase.executar(input));
    }

    @Test
    @DisplayName("Deve lançar ProdutoInativoException quando produto informado estiver inativo")
    void deveLancarExcecaoQuandoProdutoInativo() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, Collections.emptyList(), Map.of(30L, 1));

        Produto produtoInativo = Produto.builder()
                .id(30L).codigo("P-30").nome("Peça").ativo(false)
                .precoUnitario(BigDecimal.TEN).quantidadeEstoque(10).build();

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(clienteRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepositoryPort.save(any(OrdemServico.class))).thenReturn(ordemServicoSalva());
        when(produtoRepositoryPort.buscarPorId(30L)).thenReturn(Optional.of(produtoInativo));

        assertThrows(ProdutoInativoException.class, () -> useCase.executar(input));
    }

    @Test
    @DisplayName("Deve lançar ServicoInativoException quando serviço informado estiver inativo")
    void deveLancarExcecaoQuandoServicoInativo() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(
                1L, 2L, List.of(50L), Collections.emptyMap());

        Servico servicoInativo = Servico.builder()
                .id(50L).descricao("Troca").status(StatusServico.INATIVO)
                .valor(BigDecimal.valueOf(100)).build();

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(clienteRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(cliente));
        when(ordemServicoRepositoryPort.save(any(OrdemServico.class))).thenReturn(ordemServicoSalva());
        when(servicoRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(servicoInativo));

        assertThrows(ServicoInativoException.class, () -> useCase.executar(input));
    }
}
