package br.com.autocenterfiap.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Usuario implements UserDetails {

    private String usuario;
    private String senha;
    private List<Perfil> perfis = new ArrayList<>();

    public Usuario(String usuario, String senha, List<Perfil> perfis) {
        this.usuario = usuario;
        this.senha = senha;
        this.perfis = perfis;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perfis;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return usuario;
    }

}
