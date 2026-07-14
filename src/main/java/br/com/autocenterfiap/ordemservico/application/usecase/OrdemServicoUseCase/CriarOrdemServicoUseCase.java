package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.validator.OrdemServicoValidator;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;
import br.com.autocenterfiap.util.Util;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class CriarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    private final OSItemProdutoRepositoryPort osItemProdutoRepositoryPort;
    private final OSItemServicoRepositoryPort osItemServicoRepositoryPort;
    private final ProdutoRepositoryPort produtoRepositoryPort;
    private final ServicoRepositoryPort servicoRepositoryPort;
    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final List<OrdemServicoValidator> validators;

    public CriarOrdemServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort,
            OSItemProdutoRepositoryPort osItemProdutoRepositoryPort,
            OSItemServicoRepositoryPort osItemServicoRepositoryPort,
            ProdutoRepositoryPort produtoRepositoryPort,
            ServicoRepositoryPort servicoRepositoryPort,
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort,
            List<OrdemServicoValidator> validators) {
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
        this.osItemProdutoRepositoryPort = osItemProdutoRepositoryPort;
        this.osItemServicoRepositoryPort = osItemServicoRepositoryPort;
        this.produtoRepositoryPort = produtoRepositoryPort;
        this.servicoRepositoryPort = servicoRepositoryPort;
        this.veiculoRepositoryPort = veiculoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.validators = validators;
    }

    public OrdemServicoOutput executar(CriarOrdemServicoInput input) {
        validators.forEach(validator -> validator.validate(input));

        Veiculo veiculo = this.veiculoRepositoryPort.buscarPorId(input.veiculoId())
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo Não encontrado para o id: "
                        + input.veiculoId()));

        Cliente cliente = this.clienteRepositoryPort.buscarPorId(input.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado para o id: "
                        + input.clienteId()));

        OrdemServico ordemServico = OrdemServicoApplicationMapper.toEntity(input, veiculo, cliente);
        OrdemServico ordemServicoSalvo = this.ordemServicoRepositoryPort.save(ordemServico);

        adicionarItensProdutos(ordemServicoSalvo, input.produtosIdsAndQuantidades());
        adicionarItensServicos(ordemServicoSalvo, input.servicosIds());

        ordemServicoSalvo.setValorTotal(Util.calcularValorTotal(ordemServicoSalvo));
        this.ordemServicoRepositoryPort.save(ordemServicoSalvo);

        return OrdemServicoApplicationMapper.toOutput(ordemServicoSalvo);
    }

    private void adicionarItensProdutos(OrdemServico ordemServico, Map<Long, Integer> produtosIdsAndQuantidades) {
        if (produtosIdsAndQuantidades == null || produtosIdsAndQuantidades.isEmpty()) return;

        produtosIdsAndQuantidades.forEach((produtoId, quantidade) -> {
            Produto produto = this.produtoRepositoryPort.buscarPorId(produtoId)
                    .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));

            if (!produto.getAtivo()) {
                throw new ProdutoInativoException(produto.getCodigo());
            }

            produto.decrementarEstoque(quantidade);
            this.produtoRepositoryPort.salvar(produto);

            OSItemProduto item = new OSItemProduto();
            item.setOrdemServico(ordemServico);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());

            OSItemProduto itemSalvo = this.osItemProdutoRepositoryPort.save(item);
            ordemServico.getOsItensProdutos().add(itemSalvo);
        });
    }

    private void adicionarItensServicos(OrdemServico ordemServico, List<Long> servicosIds) {
        if (servicosIds == null || servicosIds.isEmpty()) return;

        servicosIds.forEach(servicoId -> {
            Servico servico = this.servicoRepositoryPort.buscarPorId(servicoId)
                    .orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado com ID: " + servicoId));

            if (servico.getStatus() == StatusServico.INATIVO) {
                log.warn("Tentativa de adicionar serviço inativo na criação da OS: ID={}, Descrição={}",
                        servico.getId(), servico.getDescricao());
                throw new ServicoInativoException(servico.getDescricao());
            }

            OSItemServico item = new OSItemServico();
            item.setOrdemServico(ordemServico);
            item.setServico(servico);
            item.setValorItemServico(servico.getValor());
            item.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);

            OSItemServico itemSalvo = this.osItemServicoRepositoryPort.save(item);
            ordemServico.getOsItensServicos().add(itemSalvo);
        });
    }
}
