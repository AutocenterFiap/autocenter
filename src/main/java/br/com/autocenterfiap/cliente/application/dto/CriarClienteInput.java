package br.com.autocenterfiap.cliente.application.dto;

import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriarClienteInput {
    private String nome;
    private TipoCliente tipoCliente;
    private String documento;
    private String email;
    private String telefone;
    private EnderecoInput endereco;
    private LocalDate dataNascimento;
}

