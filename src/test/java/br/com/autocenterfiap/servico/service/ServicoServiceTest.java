package br.com.autocenterfiap.servico.service;

import br.com.autocenterfiap.servico.dto.ServicoDto;
import br.com.autocenterfiap.servico.dto.ServicoResponseDTO;
import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.exception.ServicoNaoEncontradoException;
import br.com.autocenterfiap.servico.mapper.ServicoMapper;
import br.com.autocenterfiap.servico.model.Servico;
import br.com.autocenterfiap.servico.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServicoService - Testes Unitários")
class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @Mock
    private ServicoMapper mapper;

    @InjectMocks
    private ServicoService service;

    private Servico servico;
    private ServicoDto servicoDto;
    private ServicoResponseDTO servicoResponseDTO;

    @BeforeEach
    void setUp() {
        servico = new Servico();
        servico.setId(1L);
        servico.setDescricao("Troca de óleo");
        servico.setStatus(StatusServico.ATIVO);
        servico.setValor(BigDecimal.valueOf(100));
        servicoDto = new ServicoDto("Troca de óleo", StatusServico.ATIVO, BigDecimal.valueOf(100));
        servicoResponseDTO = ServicoResponseDTO.builder()
                .id(1L)
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void criar_DeveSalvarServico() {
        when(mapper.toEntity(servicoDto)).thenReturn(servico);
        when(repository.save(servico)).thenReturn(servico);
        when(mapper.toServicoResponseDto(servico)).thenReturn(servicoResponseDTO);
        ServicoResponseDTO salvo = service.criar(servicoDto);
        assertNotNull(salvo);
        assertEquals("Troca de óleo", salvo.descricao());
        verify(repository, times(1)).save(servico);
    }

    @Test
    void listarTodos_DeveRetornarLista() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Servico> page = new PageImpl<>(Collections.singletonList(servico), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toServicoResponseDto(servico)).thenReturn(servicoResponseDTO);
        Page<ServicoResponseDTO> lista = service.listarTodos(pageable);
        assertNotNull(lista);
        assertEquals(1, lista.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void buscarPorId_Existente_DeveRetornarServico() {
        when(repository.findById(1L)).thenReturn(Optional.of(servico));
        Servico encontrado = service.buscarEntidadePorId(1L);
        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_Inexistente_DeveLancarExcecao() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ServicoNaoEncontradoException.class, () -> service.buscarPorId(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void atualizar_DeveAtualizarServico() {
        when(repository.findById(1L)).thenReturn(Optional.of(servico));
        doNothing().when(mapper).updateEntityFromDto(servicoDto, servico);
        when(repository.save(servico)).thenReturn(servico);
        when(mapper.toServicoResponseDto(servico)).thenReturn(servicoResponseDTO);
        ServicoResponseDTO atualizado = service.atualizar(1L, servicoDto);
        assertNotNull(atualizado);
        assertEquals("Troca de óleo", atualizado.descricao());
        verify(repository, times(1)).save(servico);
    }

    @Test
    void deletar_DeveRemoverServico() {
        when(repository.findById(1L)).thenReturn(Optional.of(servico));
        doNothing().when(repository).delete(servico);

        assertDoesNotThrow(() -> service.deletar(1L));
        verify(repository, times(1)).delete(servico);
    }
}
