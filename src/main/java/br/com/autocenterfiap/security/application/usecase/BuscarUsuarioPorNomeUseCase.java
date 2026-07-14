package br.com.autocenterfiap.security.application.usecase;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.exception.UsuarioNaoEncontradoException;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Service;

@Service
public class BuscarUsuarioPorNomeUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public BuscarUsuarioPorNomeUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public UsuarioJpaEntity executar(String nome) {
        return usuarioRepositoryPort.buscarPorNome(nome)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("O usuário não foi encontrado!"));
    }
}
