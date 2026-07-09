package br.com.autocenterfiap.servico.infrastructure.config;

import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.application.usecase.*;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.adapter.ServicoRepositoryJpaAdapter;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.repository.ServicoJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.autocenterfiap.ordemservico.repository.OSItemServicoRepository;

@Configuration
public class ServicoConfiguration {

    @Bean
    public ServicoRepositoryPort servicoRepositoryPort(
        ServicoJpaRepository servicoJpaRepository,
        OSItemServicoRepository osItemServicoRepository
    ) {
        return new ServicoRepositoryJpaAdapter(servicoJpaRepository, osItemServicoRepository);
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
