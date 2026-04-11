package br.com.autocenterfiap.security.model;

import java.util.List;

public record UsuarioResponse(Long id,
                              String nome,
                              List<PerfilRequest> perfis) {
}
