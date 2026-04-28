package br.com.autocenterfiap.security.controller.response;

import br.com.autocenterfiap.security.enums.PerfilType;
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
