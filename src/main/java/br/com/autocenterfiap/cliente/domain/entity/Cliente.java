package br.com.autocenterfiap.cliente.domain.entity;

import br.com.autocenterfiap.cliente.domain.enums.StatusCliente;
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
public class Cliente {

    private Long id;
    private String nome;
    private TipoCliente tipoCliente;
    private String documento;
    private String email;
    private String telefone;
    private StatusCliente status;
    private Endereco endereco;
    private LocalDate dataNascimento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;

    public void validarDominio() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente não pode estar vazio");
        }

        if (tipoCliente == null) {
            throw new IllegalArgumentException("Tipo de cliente é obrigatório");
        }

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento não pode estar vazio");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode estar vazio");
        }

        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode estar vazio");
        }

        if (endereco == null || !endereco.isValido()) {
            throw new IllegalArgumentException("Endereço inválido ou incompleto");
        }
    }

    public void validarDocumentoPorTipo() {
        int tamanho = documento.length();
        int tamanhoEsperado = tipoCliente.getTamanhoDocumento();

        if (tamanho != tamanhoEsperado) {
            throw new IllegalArgumentException(
                String.format(
                    "Para %s, o documento deve ter %d dígitos, mas recebeu %d",
                    tipoCliente.getDescricao(),
                    tamanhoEsperado,
                    tamanho
                )
            );
        }
    }

    public boolean podeAlterarDocumento(String novoDocumento) {
        return this.documento.equals(novoDocumento);
    }

    public boolean podeAlterarTipoCliente(TipoCliente novoTipo) {
        return this.tipoCliente == novoTipo;
    }

    public void definirDataCriacao(LocalDateTime agora) {
        this.dataCriacao = agora;
    }

    public void definirDataUltimaAtualizacao(LocalDateTime agora) {
        this.dataUltimaAtualizacao = agora;
    }

    public Cliente atualizar(String nome, String email, String telefone, Endereco endereco, LocalDate dataNascimento) {
        Cliente clienteAtualizado = Cliente.builder()
            .id(this.id)
            .nome(nome)
            .tipoCliente(this.tipoCliente)
            .documento(this.documento)
            .email(email)
            .telefone(telefone)
            .status(this.status)
            .endereco(endereco)
            .dataNascimento(dataNascimento)
            .dataCriacao(this.dataCriacao)
            .dataUltimaAtualizacao(LocalDateTime.now())
            .build();

        clienteAtualizado.validarDominio();
        return clienteAtualizado;
    }

    public boolean isNovo() {
        return this.id == null;
    }
}

