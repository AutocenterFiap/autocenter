package br.com.autocenterfiap.orcamento.infrastructure.config;

import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.application.usecase.*;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.adapter.OrcamentoRepositoryJpaAdapter;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.repository.OrcamentoJpaRepository;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrcamentoConfiguration {

    @Bean
    public OrcamentoRepositoryPort orcamentoRepositoryPort(
            OrcamentoJpaRepository orcamentoJpaRepository,
            OrdemServicoJpaRepository ordemServicoJpaRepository
    ) {
        return new OrcamentoRepositoryJpaAdapter(orcamentoJpaRepository, ordemServicoJpaRepository);
    }

    @Bean
    public CriarOrcamentoUseCase criarOrcamentoUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new CriarOrcamentoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    public GerarOrcamentoUseCase gerarOrcamentoUseCase(
            OrcamentoRepositoryPort orcamentoRepositoryPort,
            CriarOrcamentoUseCase criarOrcamentoUseCase,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        return new GerarOrcamentoUseCase(orcamentoRepositoryPort, criarOrcamentoUseCase, ordemServicoRepositoryPort);
    }

    @Bean
    public BuscarOrcamentoPorIdUseCase buscarOrcamentoPorIdUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new BuscarOrcamentoPorIdUseCase(orcamentoRepositoryPort);
    }

    @Bean
    public BuscarTodosOrcamentosUseCase buscarTodosOrcamentosUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new BuscarTodosOrcamentosUseCase(orcamentoRepositoryPort);
    }

    @Bean
    public AprovarOrcamentoUseCase aprovarOrcamentoUseCase(
            OrcamentoRepositoryPort orcamentoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new AprovarOrcamentoUseCase(orcamentoRepositoryPort, ordemServicoRepositoryPort);
    }

    @Bean
    public ReprovarOrcamentoUseCase reprovarOrcamentoUseCase(
            OrcamentoRepositoryPort orcamentoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new ReprovarOrcamentoUseCase(orcamentoRepositoryPort, ordemServicoRepositoryPort);
    }

}
