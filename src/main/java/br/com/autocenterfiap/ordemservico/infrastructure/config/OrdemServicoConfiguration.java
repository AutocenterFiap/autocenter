package br.com.autocenterfiap.ordemservico.infrastructure.config;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase.*;
import br.com.autocenterfiap.ordemservico.application.validator.ClienteValidator;
import br.com.autocenterfiap.ordemservico.application.validator.OrdemServicoDuplicadaValidator;
import br.com.autocenterfiap.ordemservico.application.validator.OrdemServicoValidator;
import br.com.autocenterfiap.ordemservico.application.validator.VeiculoValidator;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.adapter.OrdemServicoRepositoryJpaAdapter;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OrdemServicoConfiguration {

    @Bean
    public OrdemServicoRepositoryPort ordemServicoRepositoryPort(OrdemServicoJpaRepository ordemServicoJpaRepository) {
        return new OrdemServicoRepositoryJpaAdapter(ordemServicoJpaRepository);
    }

    @Bean
    public CriarOrdemServicoUseCase criarOrdemServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort,
            OSItemProdutoRepositoryPort osItemProdutoRepositoryPort,
            OSItemServicoRepositoryPort osItemServicoRepositoryPort,
            ProdutoRepositoryPort produtoRepositoryPort,
            ServicoRepositoryPort servicoRepositoryPort,
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort,
            OrdemServicoDuplicadaValidator ordemServicoValidator,
            ClienteValidator clienteValidator,
            VeiculoValidator veiculoValidator
    ) {
        List<OrdemServicoValidator> validators = List.of(ordemServicoValidator, clienteValidator, veiculoValidator);
        return new CriarOrdemServicoUseCase(
                ordemServicoRepositoryPort,
                osItemProdutoRepositoryPort,
                osItemServicoRepositoryPort,
                produtoRepositoryPort,
                servicoRepositoryPort,
                veiculoRepositoryPort,
                clienteRepositoryPort,
                validators);
    }

    @Bean
    public ListarTodasOrdensServicosUseCase listarTodasOrdensServicosUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new ListarTodasOrdensServicosUseCase(ordemServicoRepositoryPort);
    }

    @Bean
    public BuscarOrdemServicoPorIdUseCase buscarOrdemServicoPorIdUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new BuscarOrdemServicoPorIdUseCase(ordemServicoRepositoryPort);
    }

    @Bean
    public BuscarOrdemServicoPorNumeroUseCase buscarOrdemServicoPorNumeroUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new BuscarOrdemServicoPorNumeroUseCase(ordemServicoRepositoryPort);
    }

    @Bean
    public AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new AtualizarOrdemServicoUseCase(ordemServicoRepositoryPort);
    }

    @Bean
    public DeletarOrdemServicoUseCase deletarOrdemServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new DeletarOrdemServicoUseCase(ordemServicoRepositoryPort);
    }

    @Bean
    public ConsultaStatusOrdemServicoUseCase consultaStatusOrdemServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new ConsultaStatusOrdemServicoUseCase(ordemServicoRepositoryPort);
    }
}
