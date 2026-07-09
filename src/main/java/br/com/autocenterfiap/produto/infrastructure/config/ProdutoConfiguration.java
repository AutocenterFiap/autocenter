package br.com.autocenterfiap.produto.infrastructure.config;

import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.application.usecase.*;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.adapter.ProdutoRepositoryJpaAdapter;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.repository.ProdutoJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProdutoConfiguration {

    @Bean
    public ProdutoRepositoryPort produtoRepositoryPort(ProdutoJpaRepository produtoJpaRepository) {
        return new ProdutoRepositoryJpaAdapter(produtoJpaRepository);
    }

    @Bean
    public ListarProdutosUseCase listarProdutosUseCase(ProdutoRepositoryPort repositoryPort) {
        return new ListarProdutosUseCase(repositoryPort);
    }

    @Bean
    public BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase(ProdutoRepositoryPort repositoryPort) {
        return new BuscarProdutoPorIdUseCase(repositoryPort);
    }

    @Bean
    public CriarProdutoUseCase criarProdutoUseCase(ProdutoRepositoryPort repositoryPort) {
        return new CriarProdutoUseCase(repositoryPort);
    }

    @Bean
    public AtualizarProdutoUseCase atualizarProdutoUseCase(ProdutoRepositoryPort repositoryPort) {
        return new AtualizarProdutoUseCase(repositoryPort);
    }

    @Bean
    public DesativarProdutoUseCase desativarProdutoUseCase(ProdutoRepositoryPort repositoryPort) {
        return new DesativarProdutoUseCase(repositoryPort);
    }

    @Bean
    public AdicionarEstoqueUseCase adicionarEstoqueUseCase(ProdutoRepositoryPort repositoryPort) {
        return new AdicionarEstoqueUseCase(repositoryPort);
    }

    @Bean
    public RemoverEstoqueUseCase removerEstoqueUseCase(ProdutoRepositoryPort repositoryPort) {
        return new RemoverEstoqueUseCase(repositoryPort);
    }

    @Bean
    public ListarAlertasEstoqueUseCase listarAlertasEstoqueUseCase(ProdutoRepositoryPort repositoryPort) {
        return new ListarAlertasEstoqueUseCase(repositoryPort);
    }
}
