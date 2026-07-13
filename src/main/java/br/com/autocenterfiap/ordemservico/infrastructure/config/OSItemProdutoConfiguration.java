package br.com.autocenterfiap.ordemservico.infrastructure.config;

import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.AdicionarProdutoNaOrdemServicoUseCase;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.AtualizarQuantidadeUseCase;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.ListarTodosPorOrdemServicoUseCase;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase.RemoverProdutoNaOrdemServicoUseCase;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.adapter.OSItemProdutoRepositoryJpaAdapter;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemProdutoJpaRepository;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSItemProdutoConfiguration {

    @Bean
    public OSItemProdutoRepositoryPort osItemProdutoRepositoryPort(OSItemProdutoJpaRepository osItemProdutoJpaRepository) {
        return new OSItemProdutoRepositoryJpaAdapter(osItemProdutoJpaRepository);
    }

    @Bean
    public AdicionarProdutoNaOrdemServicoUseCase adicionarProdutoNaOrdemServicoUseCase(
            OSItemProdutoRepositoryPort osItemProdutoRepositoryPort,
            ProdutoRepositoryPort produtoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort
    ) {
        return new AdicionarProdutoNaOrdemServicoUseCase(
                osItemProdutoRepositoryPort,
                produtoRepositoryPort,
                ordemServicoRepositoryPort
        );
    }

    @Bean
    public AtualizarQuantidadeUseCase atualizarQuantidadeUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort,
                                                                  OrdemServicoRepositoryPort ordemServicoRepositoryPort,
                                                                  ProdutoRepositoryPort produtoRepositoryPort)
    {
        return new AtualizarQuantidadeUseCase(itemProdutoRepositoryPort, ordemServicoRepositoryPort, produtoRepositoryPort);
    }

    @Bean("listarTodosProdutosPorOrdemServicoUseCase")
    public ListarTodosPorOrdemServicoUseCase listarTodosProdutosPorOrdemServicoUseCase(
            OSItemProdutoRepositoryPort itemProdutoRepositoryPort)
    {
        return new ListarTodosPorOrdemServicoUseCase(itemProdutoRepositoryPort);
    }

    @Bean
    public RemoverProdutoNaOrdemServicoUseCase removerProdutoNaOrdemServicoUseCase(
            OSItemProdutoRepositoryPort itemProdutoRepositoryPort,
            ProdutoRepositoryPort produtoRepositoryPort)
    {
        return new RemoverProdutoNaOrdemServicoUseCase(itemProdutoRepositoryPort, produtoRepositoryPort);
    }
}
