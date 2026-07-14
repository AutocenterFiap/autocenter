package br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity;

import br.com.autocenterfiap.security.domain.enums.PerfilType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "perfil")
public class PerfilJpaEntity implements GrantedAuthority, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PerfilType nome;

    @ManyToMany(mappedBy = "perfis")
    @JsonIgnoreProperties("perfis")
    private List<UsuarioJpaEntity> usuarios = new ArrayList<>();

    @Override
    public String getAuthority() {
        return  "ROLE_" + nome;
    }
}
