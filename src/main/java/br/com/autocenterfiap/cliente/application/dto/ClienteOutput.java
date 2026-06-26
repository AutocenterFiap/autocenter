package br.com.autocenterfiap.cliente.application.dto;

import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteOutput {
    private Long id;
    private String nome;
    private TipoCliente tipoCliente;
    private String documento;
    private String email;
    private String telefone;
    private EnderecoOutput endereco;
    private LocalDate dataNascimento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
}

