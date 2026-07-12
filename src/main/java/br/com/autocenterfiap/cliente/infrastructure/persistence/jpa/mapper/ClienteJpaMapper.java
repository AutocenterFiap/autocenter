package br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.entity.Endereco;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.ClienteJpaEntity;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.EnderecoJpaEntity;

public class ClienteJpaMapper {

    public static Cliente toDomain(ClienteJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        Endereco endereco = jpaEntity.getEndereco() != null
            ? toDomainEndereco(jpaEntity.getEndereco())
            : null;

        return Cliente.builder()
            .id(jpaEntity.getId())
            .nome(jpaEntity.getNome())
            .tipoCliente(jpaEntity.getTipoCliente())
            .documento(jpaEntity.getDocumento())
            .email(jpaEntity.getEmail())
            .telefone(jpaEntity.getTelefone())
            .endereco(endereco)
            .dataNascimento(jpaEntity.getDataNascimento())
            .dataCriacao(jpaEntity.getDataCriacao())
            .dataUltimaAtualizacao(jpaEntity.getDataUltimaAtualizacao())
            .build();
    }

    public static ClienteJpaEntity toJpa(Cliente domain) {
        if (domain == null) return null;

        EnderecoJpaEntity endereco = domain.getEndereco() != null
            ? toJpaEndereco(domain.getEndereco())
            : null;

        ClienteJpaEntity jpaEntity = new ClienteJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setNome(domain.getNome());
        jpaEntity.setTipoCliente(domain.getTipoCliente());
        jpaEntity.setDocumento(domain.getDocumento());
        jpaEntity.setEmail(domain.getEmail());
        jpaEntity.setTelefone(domain.getTelefone());
        jpaEntity.setEndereco(endereco);
        jpaEntity.setDataNascimento(domain.getDataNascimento());
        jpaEntity.setDataCriacao(domain.getDataCriacao());
        jpaEntity.setDataUltimaAtualizacao(domain.getDataUltimaAtualizacao());

        return jpaEntity;
    }

    private static Endereco toDomainEndereco(EnderecoJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return new Endereco(
            jpaEntity.getCep(),
            jpaEntity.getLogradouro(),
            jpaEntity.getNumero(),
            jpaEntity.getComplemento(),
            jpaEntity.getBairro(),
            jpaEntity.getCidade(),
            jpaEntity.getEstado()
        );
    }

    private static EnderecoJpaEntity toJpaEndereco(Endereco domain) {
        if (domain == null) return null;

        return new EnderecoJpaEntity(
            domain.getCep(),
            domain.getLogradouro(),
            domain.getNumero(),
            domain.getComplemento(),
            domain.getBairro(),
            domain.getCidade(),
            domain.getEstado()
        );
    }
}

