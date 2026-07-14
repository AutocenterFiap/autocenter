package br.com.autocenterfiap.security.infrastructure.security;

import br.com.autocenterfiap.security.application.port.TokenPort;
import br.com.autocenterfiap.security.application.usecase.BuscarUsuarioComPerfisUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroTokenAcesso extends OncePerRequestFilter {

    private final TokenPort tokenPort;
    private final BuscarUsuarioComPerfisUseCase buscarUsuarioComPerfisUseCase;

    public FiltroTokenAcesso(TokenPort tokenPort, BuscarUsuarioComPerfisUseCase buscarUsuarioComPerfisUseCase) {
        this.tokenPort = tokenPort;
        this.buscarUsuarioComPerfisUseCase = buscarUsuarioComPerfisUseCase;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //recuperar o token da requisição
        String token = recuperarTokenRequisicao(request);

        if(token != null){
            String nomeUsuario = tokenPort.verificarToken(token);
            var usuario = buscarUsuarioComPerfisUseCase.executar(nomeUsuario);

            Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarTokenRequisicao(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null){
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
