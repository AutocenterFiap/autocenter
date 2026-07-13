package br.com.autocenterfiap.cliente.adapter.mapper;

import br.com.autocenterfiap.cliente.application.dto.*;
import br.com.autocenterfiap.cliente.adapter.in.dto.ClienteRequestDTO;
import br.com.autocenterfiap.cliente.adapter.in.dto.ClienteResponseDTO;
import br.com.autocenterfiap.cliente.adapter.in.dto.EnderecoDTO;

public class ClienteAdapterMapper {

    public static CriarClienteInput criarClienteRequestToCriarClienteInput(ClienteRequestDTO request) {
        if (request == null) return null;

        return CriarClienteInput.builder()
            .nome(request.getNome())
            .tipoCliente(request.getTipoCliente())
            .documento(request.getDocumento())
            .email(request.getEmail())
            .telefone(request.getTelefone())
            .endereco(enderecoRequestToEnderecoInput(request.getEndereco()))
            .dataNascimento(request.getDataNascimento())
            .build();
    }

    public static AtualizarClienteInput atualizarClienteRequestToAtualizarClienteInput(ClienteRequestDTO request) {
        if (request == null) return null;

        return AtualizarClienteInput.builder()
            .nome(request.getNome())
            .documento(request.getDocumento())
            .email(request.getEmail())
            .telefone(request.getTelefone())
            .endereco(enderecoRequestToEnderecoInput(request.getEndereco()))
            .dataNascimento(request.getDataNascimento())
            .build();
    }

    private static EnderecoInput enderecoRequestToEnderecoInput(EnderecoDTO endereco) {
        if (endereco == null) return null;

        return EnderecoInput.builder()
            .cep(endereco.getCep())
            .logradouro(endereco.getLogradouro())
            .numero(endereco.getNumero())
            .complemento(endereco.getComplemento())
            .bairro(endereco.getBairro())
            .cidade(endereco.getCidade())
            .estado(endereco.getEstado())
            .build();
    }

    public static ClienteResponseDTO clienteOutputToClienteResponse(ClienteOutput output) {
        if (output == null) return null;

        return ClienteResponseDTO.builder()
            .id(output.getId())
            .nome(output.getNome())
            .tipoCliente(output.getTipoCliente())
            .documento(output.getDocumento())
            .email(output.getEmail())
            .telefone(output.getTelefone())
            .endereco(enderecoOutputToEnderecoDTO(output.getEndereco()))
            .dataNascimento(output.getDataNascimento())
            .dataCriacao(output.getDataCriacao())
            .dataUltimaAtualizacao(output.getDataUltimaAtualizacao())
            .build();
    }

    private static EnderecoDTO enderecoOutputToEnderecoDTO(EnderecoOutput endereco) {
        if (endereco == null) return null;

        return EnderecoDTO.builder()
            .cep(endereco.getCep())
            .logradouro(endereco.getLogradouro())
            .numero(endereco.getNumero())
            .complemento(endereco.getComplemento())
            .bairro(endereco.getBairro())
            .cidade(endereco.getCidade())
            .estado(endereco.getEstado())
            .build();
    }
}

