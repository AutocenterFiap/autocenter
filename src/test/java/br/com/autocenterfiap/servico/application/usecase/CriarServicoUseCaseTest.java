package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.CriarServicoInput;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
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
@DisplayName("CriarServicoUseCase - Testes Unitários")
class CriarServicoUseCaseTest {

    @Mock
    private ServicoRepositoryPort repositoryPort;

    @InjectMocks
    private CriarServicoUseCase criarServicoUseCase;

    private CriarServicoInput inputValido;

    @BeforeEach
    void setUp() {
        inputValido = CriarServicoInput.builder()
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso quando todos os dados forem válidos")
    void deveCriarServicoComSucesso() {
        when(repositoryPort.salvar(any(Servico.class))).thenAnswer(invocation -> {
            Servico s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        ServicoOutput output = criarServicoUseCase.executar(inputValido);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("Troca de óleo", output.getDescricao());
        assertEquals(StatusServico.ATIVO, output.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), output.getValor());
        verify(repositoryPort, times(1)).salvar(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando campos obrigatórios do domínio forem nulos/vazios")
    void deveLancarExcecaoQuandoCamposObrigatoriosForemInvalidos() {
        // Descrição vazia
        CriarServicoInput inputDescricaoVazia = CriarServicoInput.builder()
                .descricao("")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100.00))
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarServicoUseCase.executar(inputDescricaoVazia));

        // Valor nulo
        CriarServicoInput inputValorNulo = CriarServicoInput.builder()
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarServicoUseCase.executar(inputValorNulo));

        // Valor negativo
        CriarServicoInput inputValorNegativo = CriarServicoInput.builder()
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(-10.00))
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarServicoUseCase.executar(inputValorNegativo));
    }
}
