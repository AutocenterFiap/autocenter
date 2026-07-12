package br.com.autocenterfiap.cliente.infrastructure.config;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.application.port.ValidadorDocumentoPort;
import br.com.autocenterfiap.cliente.application.usecase.*;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.adapter.ClienteRepositoryJpaAdapter;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.repository.ClienteJpaRepository;
import br.com.autocenterfiap.cliente.infrastructure.validator.adapter.ValidadorDocumentoAdapter;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteConfiguration {

    @Bean
    public ClienteRepositoryPort clienteRepositoryPort(
        ClienteJpaRepository clienteJpaRepository,
        OrdemServicoJpaRepository ordemServicoJpaRepository
    ) {
        return new ClienteRepositoryJpaAdapter(clienteJpaRepository, ordemServicoJpaRepository);
    }

    @Bean
    public ValidadorDocumentoPort validadorDocumentoPort() {
        return new ValidadorDocumentoAdapter();
    }

    @Bean
    public CriarClienteUseCase criarClienteUseCase(
        ClienteRepositoryPort clienteRepositoryPort,
        ValidadorDocumentoPort validadorDocumentoPort
    ) {
        return new CriarClienteUseCase(clienteRepositoryPort, validadorDocumentoPort);
    }

    @Bean
    public BuscarClientePorIdUseCase buscarClientePorIdUseCase(
        ClienteRepositoryPort clienteRepositoryPort
    ) {
        return new BuscarClientePorIdUseCase(clienteRepositoryPort);
    }

    @Bean
    public BuscarClientePorDocumentoUseCase buscarClientePorDocumentoUseCase(
        ClienteRepositoryPort clienteRepositoryPort
    ) {
        return new BuscarClientePorDocumentoUseCase(clienteRepositoryPort);
    }

    @Bean
    public ListarClientesUseCase listarClientesUseCase(
        ClienteRepositoryPort clienteRepositoryPort
    ) {
        return new ListarClientesUseCase(clienteRepositoryPort);
    }

    @Bean
    public AtualizarClienteUseCase atualizarClienteUseCase(
        ClienteRepositoryPort clienteRepositoryPort
    ) {
        return new AtualizarClienteUseCase(clienteRepositoryPort);
    }

    @Bean
    public DeletarClienteUseCase deletarClienteUseCase(
        ClienteRepositoryPort clienteRepositoryPort
    ) {
        return new DeletarClienteUseCase(clienteRepositoryPort);
    }
}

