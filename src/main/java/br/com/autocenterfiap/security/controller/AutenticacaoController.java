package br.com.autocenterfiap.security.controller;

import br.com.autocenterfiap.security.model.Login;
import br.com.autocenterfiap.security.model.RefreshToken;
import br.com.autocenterfiap.security.model.Token;
import br.com.autocenterfiap.security.repository.UsuarioRepository;
import br.com.autocenterfiap.security.service.TokenService;
import br.com.autocenterfiap.security.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoController(UsuarioRepository usuarioRepository, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<Token> efetuarLogin(@Valid @RequestBody Login loginRequest){
        var autenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.clientId(), loginRequest.clientSecret());
        var authentication = authenticationManager.authenticate(autenticationToken);

        String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        String refreshToken = tokenService.gerarRefreshToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new Token(tokenAcesso, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Token> atualizarToken(@Valid @RequestBody RefreshToken refreshToken){
        String nomeUsuario = tokenService.verificarToken(refreshToken.refreshToken());
        var usuario = usuarioRepository.findByUsuario(nomeUsuario).orElseThrow();

        String novoTokenAcesso = tokenService.gerarToken(usuario);
        String novoRefreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new Token(novoTokenAcesso, novoRefreshToken));
    }

    @GetMapping
    public ResponseEntity<String> teste(){
        return ResponseEntity.ok("permitido");
    }
}
