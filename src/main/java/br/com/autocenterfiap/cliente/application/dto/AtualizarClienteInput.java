package br.com.autocenterfiap.cliente.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtualizarClienteInput {
    private String nome;
    private String email;
    private String telefone;
    private EnderecoInput endereco;
    private LocalDate dataNascimento;
}

