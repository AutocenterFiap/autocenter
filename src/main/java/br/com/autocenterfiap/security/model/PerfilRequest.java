package br.com.autocenterfiap.security.model;

import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.enums.PerfilType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PerfilRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private PerfilType nome;
}
