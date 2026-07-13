package br.com.autocenterfiap.util;

import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Util - calcularValorTotal")
class UtilTest {

    private OrdemServico osVazia() {
        OrdemServico os = new OrdemServico();
        os.setOsItensProdutos(new ArrayList<>());
        os.setOsItensServicos(new ArrayList<>());
        return os;
    }

    private OSItemProduto itemProduto(BigDecimal precoUnitario, int quantidade) {
        OSItemProduto item = new OSItemProduto();
        item.setPrecoUnitarioNoMomento(precoUnitario);
        item.setQuantidade(quantidade);
        return item;
    }

    private OSItemServico itemServico(BigDecimal valor) {
        OSItemServico item = new OSItemServico();
        item.setValorItemServico(valor);
        return item;
    }

    @Test
    @DisplayName("Deve retornar zero para OS sem produtos e sem serviços")
    void deveRetornarZeroParaOSVazia() {
        OrdemServico os = osVazia();

        BigDecimal resultado = Util.calcularValorTotal(os);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    @DisplayName("Deve calcular valor total somente com produtos")
    void deveCalcularValorTotalSomenteProdutos() {
        OrdemServico os = osVazia();
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
        OrdemServico os = osVazia();
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
        OrdemServico os = osVazia();
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
        OrdemServico os = osVazia();
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
