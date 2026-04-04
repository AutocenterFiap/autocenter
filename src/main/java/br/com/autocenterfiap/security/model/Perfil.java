package br.com.autocenterfiap.security.model;

import br.com.autocenterfiap.security.enums.PerfilType;
import org.springframework.security.core.GrantedAuthority;

public class Perfil implements GrantedAuthority {

    private PerfilType nome;

    @Override
    public String getAuthority() {
        return "ROLE_" + nome;
    }
}
