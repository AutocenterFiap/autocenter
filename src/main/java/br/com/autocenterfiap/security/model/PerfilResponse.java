package br.com.autocenterfiap.security.model;

import br.com.autocenterfiap.security.enums.PerfilType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class PerfilResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private PerfilType nome;
}
