package br.com.autocenterfiap.security.controller.response;

import java.util.List;

public record UsuarioResponse(Long id,
                              String nome,
                              List<PerfilResponse> perfis) {
}
