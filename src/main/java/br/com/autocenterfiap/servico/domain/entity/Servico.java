package br.com.autocenterfiap.servico.domain.entity;

import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servico {
    private Long id;
    private String descricao;
    private StatusServico status;
    private BigDecimal valor;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;

    public void validarDominio() {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do serviço é obrigatória");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status do serviço é obrigatório");
        }
        if (valor == null) {
            throw new IllegalArgumentException("Valor do serviço é obrigatório");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do serviço não pode ser negativo");
        }
    }

    public void atualizar(String descricao, StatusServico status, BigDecimal valor) {
        this.descricao = descricao;
        this.status = status;
        this.valor = valor;
        this.dataUltimaAtualizacao = LocalDateTime.now();
        validarDominio();
    }
}
