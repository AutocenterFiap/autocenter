package br.com.autocenterfiap.veiculo.service;

import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.dto.VeiculoResponseDTO;
import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.exception.*;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import br.com.autocenterfiap.veiculo.validator.ChassiValidator;
import br.com.autocenterfiap.veiculo.validator.PlacaValidator;
import br.com.autocenterfiap.veiculo.validator.RenavamValidator;
import br.com.autocenterfiap.veiculo.validator.VeiculoValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VeiculoService - Testes Unitários")
class VeiculoServiceTest {
    private VeiculoService veiculoService;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ChassiValidator chassiValidator;
    @Mock
    private RenavamValidator renavamValidator;
    @Mock
    private PlacaValidator placaValidator;

    private Veiculo veiculo;
    private VeiculoDTO veiculoDTO;
    private VeiculoDTO veiculoUpdateDTO;

    @BeforeEach
    public void setUp(){
        veiculoService = new VeiculoService(veiculoRepository,ordemServicoRepository, List.of(chassiValidator, renavamValidator,placaValidator));
        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1D23");
        veiculo.setChassi("9BWZZZ377VT004251");
        veiculo.setRenavam("82106426707");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAnoFabricacao(2020);
        veiculo.setAnoModelo(2021);
        veiculo.setCor("Preto");
        veiculo.setQuilometragem(45000L);
        veiculo.setTipoCombustivel(TipoCombustivel.DIESEL);
        veiculo.setCategoriaVeiculo(CategoriaVeiculo.CARRO);

        veiculoDTO = new VeiculoDTO(
                "ABC1D23",
                "9BWZZZ377VT004251",
                "82106426707",
                "Toyota",
                "Corolla",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO
        );

        veiculoUpdateDTO = new VeiculoDTO(
                "ABC1D23",
                "9BWZZZ377VT004251",
                "82106426707",
                "Ford",
                "Bronco",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO
        );
    }


    @Test
    public void deveRetornarVeiculoAoBuscarPorId(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        VeiculoResponseDTO result = veiculoService.buscarPorId(1L);

        assertNotNull(result);
        Assertions.assertEquals(veiculo.getId(), result.id());
        verify(veiculoRepository,times(1)).findById(1L);
    }

    @Test
    public void naoDeveRetornarVeiculoAoBuscarPorIdInexistente(){
        when(veiculoRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.buscarPorId(999L));
        verify(veiculoRepository,times(1)).findById(999L);
    }

    @Test
    public void deveRetornarVeiculoAoBuscarPorPlaca(){
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculo));
        VeiculoResponseDTO result = veiculoService.buscarPorPlaca("ABC1D23");

        assertNotNull(result);
        Assertions.assertEquals(veiculo.getPlaca(), result.placa());
        verify(veiculoRepository,times(1)).findByPlaca("ABC1D23");
    }

    @Test
    public void naoDeveRetornarVeiculoAoBuscarPorPlacaInexistente(){
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.empty());
        Assertions.assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.buscarPorPlaca("ABC1D23"));
        verify(veiculoRepository,times(1)).findByPlaca("ABC1D23");
    }

    @Test
    public void deveRetornarListaDeVeiculosAoListarTodos(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> paginaVeiculos = new PageImpl<>(List.of(veiculo));
        
        when(veiculoRepository.findAll(any(Pageable.class))).thenReturn(paginaVeiculos);
        
        Page<VeiculoResponseDTO> result = veiculoService.listarTodos(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(veiculoRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void deveRetornarListaDeVeiculosVaziaAoListarTodos(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> paginaVazia = Page.empty();
        
        when(veiculoRepository.findAll(any(Pageable.class))).thenReturn(paginaVazia);
        
        Page<VeiculoResponseDTO> result = veiculoService.listarTodos(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(veiculoRepository,times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void deveCriarVeiculoComSucesso(){
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        VeiculoResponseDTO result = veiculoService.criar(veiculoDTO);

        assertNotNull(result);
        assertEquals(veiculo.getPlaca(), result.placa());
        
        verify(chassiValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(renavamValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(placaValidator, times(1)).validate(any(VeiculoValidationContext.class));

        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoCriarVeiculoComPlacaJaCadastrada(){
        doThrow(new PlacaJaCadastradaException("")).when(placaValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(PlacaJaCadastradaException.class,() -> veiculoService.criar(veiculoDTO));
        verify(placaValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoCriarVeiculoComRenavamJaCadastrado(){
        doThrow(new RenavamJaCadastradoException("")).when(renavamValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(RenavamJaCadastradoException.class,() -> veiculoService.criar(veiculoDTO));
        verify(renavamValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoCriarVeiculoComChassiJaCadastrado(){
        doThrow(new ChassiJaCadastradoException("")).when(chassiValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(ChassiJaCadastradoException.class,() -> veiculoService.criar(veiculoDTO));
        verify(chassiValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoCriarVeiculoComChassiInvalido(){
        doThrow(new ChassiInvalidoException("")).when(chassiValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(ChassiInvalidoException.class,() -> veiculoService.criar(veiculoDTO));
        verify(chassiValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoCriarVeiculoComRenavamInvalido(){
        doThrow(new RenavamInvalidoException("")).when(renavamValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(RenavamInvalidoException.class,() -> veiculoService.criar(veiculoDTO));
        verify(renavamValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveAtualizarVeiculoComSucesso(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        VeiculoResponseDTO result = veiculoService.atualizar(1L,veiculoUpdateDTO);

        assertNotNull(result);
        assertEquals(veiculo.getMarca(), result.marca());
        assertEquals(veiculo.getModelo(), result.modelo());

        verify(chassiValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(renavamValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(placaValidator, times(1)).validate(any(VeiculoValidationContext.class));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarVeiculoInexistente(){
        when(veiculoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.atualizar(999L,veiculoUpdateDTO));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarVeiculoComPlacaJaCadastrada(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        doThrow(new PlacaJaCadastradaException("")).when(placaValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(PlacaJaCadastradaException.class,() -> veiculoService.atualizar(1L,veiculoUpdateDTO));
        verify(placaValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarVeiculoComRenavamJaCadastrado(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        doThrow(new RenavamJaCadastradoException("")).when(renavamValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(RenavamJaCadastradoException.class,() -> veiculoService.atualizar(1L,veiculoUpdateDTO));
        verify(renavamValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarVeiculoComChassiJaCadastrado(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        doThrow(new ChassiJaCadastradoException("")).when(chassiValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(ChassiJaCadastradoException.class,() -> veiculoService.atualizar(1L,veiculoUpdateDTO));
        verify(chassiValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarVeiculoComChassiInvalido(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        doThrow(new ChassiInvalidoException("")).when(chassiValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(ChassiInvalidoException.class,() -> veiculoService.atualizar(1L,veiculoUpdateDTO));
        verify(chassiValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarVeiculoComRenavamInvalido(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        doThrow(new RenavamInvalidoException("")).when(renavamValidator).validate(any(VeiculoValidationContext.class));
        assertThrows(RenavamInvalidoException.class,() -> veiculoService.atualizar(1L,veiculoUpdateDTO));
        verify(renavamValidator, times(1)).validate(any(VeiculoValidationContext.class));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    public void deveDeletarVeiculoComSucesso(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.existsByVeiculoId(1L)).thenReturn(false);
        doNothing().when(veiculoRepository).delete(any(Veiculo.class));

        veiculoService.deletar(1L);

        verify(veiculoRepository, times(1)).findById(1L);
        verify(ordemServicoRepository, times(1)).existsByVeiculoId(1L);
        verify(veiculoRepository, times(1)).delete(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoDeletarVeiculoEmUso(){
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.existsByVeiculoId(1L)).thenReturn(true);

        assertThrows(VeiculoEmUsoException.class, () -> veiculoService.deletar(1L));

        verify(veiculoRepository, times(1)).findById(1L);
        verify(ordemServicoRepository, times(1)).existsByVeiculoId(1L);
        verify(veiculoRepository, never()).delete(any(Veiculo.class));
    }

    @Test
    public void deveLancarExcecaoAoDeletarVeiculoInexistente(){
        when(veiculoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.deletar(999L));
        verify(veiculoRepository, times(1)).findById(999L);
    }

}