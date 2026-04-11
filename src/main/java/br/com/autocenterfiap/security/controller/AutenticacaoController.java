package br.com.autocenterfiap.security.controller;

import br.com.autocenterfiap.security.model.LoginRequest;
import br.com.autocenterfiap.security.model.RefreshToken;
import br.com.autocenterfiap.security.model.Token;
import br.com.autocenterfiap.security.repository.UsuarioRepository;
import br.com.autocenterfiap.security.service.TokenService;
import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AutenticacaoController {
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @PostMapping("/token")
    public ResponseEntity<Token> efetuarLogin(@Valid @RequestBody LoginRequest loginRequest){
        var autenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.nome(), loginRequest.senha());
        var authentication = authenticationManager.authenticate(autenticationToken);

        String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        String refreshToken = tokenService.gerarRefreshToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new Token(tokenAcesso, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Token> atualizarToken(@Valid @RequestBody RefreshToken refreshToken){
        String nomeUsuario = tokenService.verificarToken(refreshToken.refreshToken());
        var usuario = usuarioService.findByNome(nomeUsuario);

        String novoTokenAcesso = tokenService.gerarToken(usuario);
        String novoRefreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new Token(novoTokenAcesso, novoRefreshToken));
    }

    @GetMapping("/clientes/{clienteId}")
    public ResponseEntity<String> teste(String clienteId){
        return ResponseEntity.ok("permitido");
    }


}
