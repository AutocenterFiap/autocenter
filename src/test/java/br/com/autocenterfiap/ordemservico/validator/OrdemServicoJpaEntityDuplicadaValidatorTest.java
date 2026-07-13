package br.com.autocenterfiap.ordemservico.validator;

import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoJaAbertaParaVeiculoException;
import br.com.autocenterfiap.ordemservico.application.validator.OrdemServicoDuplicadaValidator;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
@DisplayName("Ordem Duplicada Validator - Testes Unitários")
class OrdemServicoJpaEntityDuplicadaValidatorTest {
    @InjectMocks
    private OrdemServicoDuplicadaValidator ordemServicoDuplicadaValidator;

    @Mock
    private OrdemServicoJpaRepository ordemServicoJpaRepository;


    @Test
    public void devePassarQuandoNaoExistirOrdemServicoAbertaParaVeiculo(){
        OrdemServicoDTO dto = new OrdemServicoDTO(1L, 1L);
        doReturn(false).when(ordemServicoJpaRepository).existsByVeiculoIdAndStatusOSIn(eq(1L), anyList());
        assertDoesNotThrow(() -> ordemServicoDuplicadaValidator.validate(dto));
    }

    @Test
    public void deveLancarExceptionQuandoExistirOrdemServicoAbertaParaVeiculo(){
        OrdemServicoDTO dto = new OrdemServicoDTO(1L, 1L);
        doReturn(true).when(ordemServicoJpaRepository).existsByVeiculoIdAndStatusOSIn(eq(1L), anyList());
        assertThrows(OrdemServicoJaAbertaParaVeiculoException.class,() -> ordemServicoDuplicadaValidator.validate(dto));
    }
}