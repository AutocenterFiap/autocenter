package br.com.autocenterfiap.orcamento.model;

import br.com.autocenterfiap.orcamento.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrcamentoTest {

    @Test
    void prePersist_deveDefinirStatusGeradoQuandoStatusNulo() {
       Orcamento orcamento = Orcamento.builder().build();
        orcamento.prePersist();
        assertEquals(StatusOrcamento.GERADO, orcamento.getStatusOrcamento());
        assertNotNull(orcamento.getDataCriacao());
        assertNotNull(orcamento.getDataUltimaAtualizacao());
    }

    @Test
    void preUpdate_deveAtualizarDataUltimaAtualizacao() {
        Orcamento orcamento = Orcamento.builder().build();
        orcamento.prePersist();
        LocalDateTime dataAnterior = orcamento.getDataUltimaAtualizacao();
        orcamento.preUpdate();
        assertTrue(orcamento.getDataUltimaAtualizacao().isAfter(dataAnterior) ||
                orcamento.getDataUltimaAtualizacao().isEqual(dataAnterior));
    }
}