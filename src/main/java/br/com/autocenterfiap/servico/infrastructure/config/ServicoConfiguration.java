package br.com.autocenterfiap.servico.infrastructure.config;

import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.application.usecase.*;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.adapter.ServicoRepositoryJpaAdapter;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.repository.ServicoJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemServicoJpaRepository;

@Configuration
public class ServicoConfiguration {

    @Bean
    public ServicoRepositoryPort servicoRepositoryPort(
        ServicoJpaRepository servicoJpaRepository,
        OSItemServicoJpaRepository osItemServicoJpaRepository
    ) {
        return new ServicoRepositoryJpaAdapter(servicoJpaRepository, osItemServicoJpaRepository);
    }

    @Bean
    public CriarServicoUseCase criarServicoUseCase(ServicoRepositoryPort repositoryPort) {
        return new CriarServicoUseCase(repositoryPort);
    }

    @Bean
    public BuscarServicoPorIdUseCase buscarServicoPorIdUseCase(ServicoRepositoryPort repositoryPort) {
        return new BuscarServicoPorIdUseCase(repositoryPort);
    }

    @Bean
    public ListarServicosUseCase listarServicosUseCase(ServicoRepositoryPort repositoryPort) {
        return new ListarServicosUseCase(repositoryPort);
    }

    @Bean
    public ListarServicosPorStatusUseCase listarServicosPorStatusUseCase(ServicoRepositoryPort repositoryPort) {
        return new ListarServicosPorStatusUseCase(repositoryPort);
    }

    @Bean
    public AtualizarServicoUseCase atualizarServicoUseCase(ServicoRepositoryPort repositoryPort) {
        return new AtualizarServicoUseCase(repositoryPort);
    }

    @Bean
    public DeletarServicoUseCase deletarServicoUseCase(ServicoRepositoryPort repositoryPort) {
        return new DeletarServicoUseCase(repositoryPort);
    }
}
