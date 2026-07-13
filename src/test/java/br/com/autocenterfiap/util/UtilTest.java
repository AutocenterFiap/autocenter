package br.com.autocenterfiap.util;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemProdutoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Util - calcularValorTotal")
class UtilTest {

    private OrdemServicoJpaEntity osVazia() {
        OrdemServicoJpaEntity os = new OrdemServicoJpaEntity();
        os.setOsItensProdutos(new ArrayList<>());
        os.setOsItensServicos(new ArrayList<>());
        return os;
    }

    private OSItemProdutoJpaEntity itemProduto(BigDecimal precoUnitario, int quantidade) {
        OSItemProdutoJpaEntity item = new OSItemProdutoJpaEntity();
        item.setPrecoUnitarioNoMomento(precoUnitario);
        item.setQuantidade(quantidade);
        return item;
    }

    private OSItemServicoJpaEntity itemServico(BigDecimal valor) {
        OSItemServicoJpaEntity item = new OSItemServicoJpaEntity();
        item.setValorItemServico(valor);
        return item;
    }

    @Test
    @DisplayName("Deve retornar zero para OS sem produtos e sem serviços")
    void deveRetornarZeroParaOSVazia() {
        OrdemServicoJpaEntity os = osVazia();

        BigDecimal resultado = Util.calcularValorTotal(os);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    @DisplayName("Deve calcular valor total somente com produtos")
    void deveCalcularValorTotalSomenteProdutos() {
        OrdemServicoJpaEntity os = osVazia();
        // Produto 1: R$ 45,90 x 2 = R$ 91,80
        // Produto 2: R$ 100,00 x 3 = R$ 300,00
        // Total esperado: R$ 391,80
        os.setOsItensProdutos(List.of(
                itemProduto(new BigDecimal("45.90"), 2),
                itemProduto(new BigDecimal("100.00"), 3)
        ));

        BigDecimal resultado = Util.calcularValorTotal(os);

        assertEquals(new BigDecimal("391.80"), resultado);
    }

    @Test
    @DisplayName("Deve calcular valor total somente com serviços")
    void deveCalcularValorTotalSomenteServicos() {
        OrdemServicoJpaEntity os = osVazia();
        // Serviço 1: R$ 200,00
        // Serviço 2: R$ 150,50
        // Total esperado: R$ 350,50
        os.setOsItensServicos(List.of(
                itemServico(new BigDecimal("200.00")),
                itemServico(new BigDecimal("150.50"))
        ));

        BigDecimal resultado = Util.calcularValorTotal(os);

        assertEquals(new BigDecimal("350.50"), resultado);
    }

    @Test
    @DisplayName("Deve calcular valor total com produtos e serviços")
    void deveCalcularValorTotalComProdutosEServicos() {
        OrdemServicoJpaEntity os = osVazia();
        // Produto: R$ 50,00 x 4 = R$ 200,00
        // Serviço: R$ 300,00
        // Total esperado: R$ 500,00
        os.setOsItensProdutos(List.of(itemProduto(new BigDecimal("50.00"), 4)));
        os.setOsItensServicos(List.of(itemServico(new BigDecimal("300.00"))));

        BigDecimal resultado = Util.calcularValorTotal(os);

        assertEquals(new BigDecimal("500.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular valor total com múltiplos produtos e múltiplos serviços")
    void deveCalcularValorTotalComMultiplosProdutosEServicos() {
        OrdemServicoJpaEntity os = osVazia();
        // Produto 1: R$ 10,00 x 5 = R$ 50,00
        // Produto 2: R$ 20,00 x 2 = R$ 40,00
        // Serviço 1: R$ 80,00
        // Serviço 2: R$ 30,00
        // Total esperado: R$ 200,00
        os.setOsItensProdutos(List.of(
                itemProduto(new BigDecimal("10.00"), 5),
                itemProduto(new BigDecimal("20.00"), 2)
        ));
        os.setOsItensServicos(List.of(
                itemServico(new BigDecimal("80.00")),
                itemServico(new BigDecimal("30.00"))
        ));

        BigDecimal resultado = Util.calcularValorTotal(os);

        assertEquals(new BigDecimal("200.00"), resultado);
    }
}
