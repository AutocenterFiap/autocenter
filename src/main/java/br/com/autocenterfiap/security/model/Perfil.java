package br.com.autocenterfiap.security.model;

import br.com.autocenterfiap.security.enums.PerfilType;
import org.springframework.security.core.GrantedAuthority;

public class Perfil implements GrantedAuthority {
    private PerfilType nome;

    public Perfil(PerfilType nome) {
        this.nome = nome;
    }

    @Override
    public String getAuthority() {
        return nome.name();
    }
}
