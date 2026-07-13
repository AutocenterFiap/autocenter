package br.com.autocenterfiap.ordemservico.infrastructure.config;

import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase.*;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.adapter.OSItemServicoRepositoryJpaAdapter;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemServicoJpaRepository;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSItemServicoConfiguration {

    @Bean
    public OSItemServicoRepositoryPort osItemServicoRepositoryPort(OSItemServicoJpaRepository osItemServicoJpaRepository) {
        return new OSItemServicoRepositoryJpaAdapter(osItemServicoJpaRepository);
    }

    @Bean
    public AdicionarServicoOrdemServicoUseCase adicionarServicoOrdemServicoUseCase (
            OSItemServicoRepositoryPort osItemServicoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort,
            ServicoRepositoryPort servicoRepositoryPort
    ) {
        return new AdicionarServicoOrdemServicoUseCase(osItemServicoRepositoryPort, ordemServicoRepositoryPort, servicoRepositoryPort);
    }

    @Bean
    public BuscarMetricasTempoGastoServicoUseCase buscarMetricasTempoGastoServicoUseCase(
            OSItemServicoRepositoryPort itemServicoRepositoryPort)
    {
        return new BuscarMetricasTempoGastoServicoUseCase(itemServicoRepositoryPort);
    }

    @Bean
    public FinalizarServicoUseCase finalizarServicoUseCase(OSItemServicoRepositoryPort itemServicoRepositoryPort) {
        return new FinalizarServicoUseCase(itemServicoRepositoryPort);
    }

    @Bean
    public IniciarServicoUseCase iniciarServicoUseCase(
            OSItemServicoRepositoryPort itemServicoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new IniciarServicoUseCase(itemServicoRepositoryPort, ordemServicoRepositoryPort);
    }

    @Bean("listarTodosServicosPorOrdemServicoUseCase")
    public ListarTodosPorOrdemServicoUseCase listarTodosServicosPorOrdemServicoUseCase(
            OSItemServicoRepositoryPort itemServicoRepositoryPort
    ) {
        return new ListarTodosPorOrdemServicoUseCase(itemServicoRepositoryPort);
    }

    @Bean
    public RemoverServicoDaOrdemServicoUseCase removerServicoDaOrdemServicoUseCase(
            OSItemServicoRepositoryPort osItemServicoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new RemoverServicoDaOrdemServicoUseCase(osItemServicoRepositoryPort, ordemServicoRepositoryPort);
    }
}
