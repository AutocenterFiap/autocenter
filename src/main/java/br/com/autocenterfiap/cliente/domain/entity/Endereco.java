package br.com.autocenterfiap.cliente.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    public boolean isValido() {
        return cep != null && !cep.isBlank() &&
                logradouro != null && !logradouro.isBlank() &&
                cidade != null && !cidade.isBlank() &&
                estado != null && !estado.isBlank();
    }
}

