package br.com.autocenterfiap.cliente.application.mapper;

import br.com.autocenterfiap.cliente.application.dto.*;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.entity.Endereco;

public class ClienteApplicationMapper {

    public static Cliente toEntity(CriarClienteInput input) {
        if (input == null) return null;

        Endereco endereco = toEnderecoEntity(input.getEndereco());

        return Cliente.builder()
            .nome(input.getNome())
            .tipoCliente(input.getTipoCliente())
            .documento(input.getDocumento())
            .email(input.getEmail())
            .telefone(input.getTelefone())
            .endereco(endereco)
            .dataNascimento(input.getDataNascimento())
            .build();
    }

    private static Endereco toEnderecoEntity(EnderecoInput input) {
        if (input == null) return null;

        return new Endereco(
            input.getCep(),
            input.getLogradouro(),
            input.getNumero(),
            input.getComplemento(),
            input.getBairro(),
            input.getCidade(),
            input.getEstado()
        );
    }

    public static Endereco toEnderecoEntity(AtualizarClienteInput input) {
        if (input == null || input.getEndereco() == null) return null;

        return toEnderecoEntity(input.getEndereco());
    }

    public static ClienteOutput toOutput(Cliente entity) {
        if (entity == null) return null;

        EnderecoOutput endereco = toEnderecoOutput(entity.getEndereco());

        return ClienteOutput.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .tipoCliente(entity.getTipoCliente())
            .documento(entity.getDocumento())
            .email(entity.getEmail())
            .telefone(entity.getTelefone())
            .endereco(endereco)
            .dataNascimento(entity.getDataNascimento())
            .dataCriacao(entity.getDataCriacao())
            .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
            .build();
    }

    private static EnderecoOutput toEnderecoOutput(Endereco entity) {
        if (entity == null) return null;

        return EnderecoOutput.builder()
            .cep(entity.getCep())
            .logradouro(entity.getLogradouro())
            .numero(entity.getNumero())
            .complemento(entity.getComplemento())
            .bairro(entity.getBairro())
            .cidade(entity.getCidade())
            .estado(entity.getEstado())
            .build();
    }
}

