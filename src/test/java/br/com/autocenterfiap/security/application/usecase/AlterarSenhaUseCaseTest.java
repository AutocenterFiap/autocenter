package br.com.autocenterfiap.security.application.usecase;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarSenhaUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private AlterarSenhaUseCase alterarSenhaUseCase;

    private UsuarioJpaEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = createUsuarioMock(1L, "maria", "senha123", createPerfisMock(PerfilType.ADMIN));
    }

    @Test
    void deveAlterarSenhaDoUsuario() {
        UsuarioJpaEntity usuarioAlteracao = new UsuarioJpaEntity();
        usuarioAlteracao.setNome("maria");
        usuarioAlteracao.setSenha("novaSenha");

        when(usuarioRepositoryPort.buscarPorNome("maria")).thenReturn(Optional.of(usuario));
        when(usuarioRepositoryPort.salvar(any(UsuarioJpaEntity.class))).thenReturn(usuarioAlteracao);

        UsuarioJpaEntity atualizado = alterarSenhaUseCase.executar(usuarioAlteracao);

        assertEquals("novaSenha", atualizado.getSenha());
        verify(usuarioRepositoryPort, times(1)).salvar(usuarioAlteracao);
    }
}
