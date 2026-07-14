package br.com.autocenterfiap.security.application.port;

public interface TokenPort {

    String gerarToken(String username);

    String gerarRefreshToken(String username);

    String verificarToken(String token);

    void limparCache(String username);
}
