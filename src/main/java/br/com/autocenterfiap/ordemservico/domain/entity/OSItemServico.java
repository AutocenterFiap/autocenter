package br.com.autocenterfiap.ordemservico.domain.entity;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OSItemServico implements Serializable {

    private Long id;

    private OrdemServico ordemServico;

    private Servico servico;

    private BigDecimal valorItemServico;

    private StatusItemServico statusServico;

    private LocalDateTime dataHoraInicio;

    private LocalDateTime dataHoraFim;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataUltimaAtualizacao;
}