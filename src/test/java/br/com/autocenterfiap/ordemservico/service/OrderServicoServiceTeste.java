package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.exception.StatusOSInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusOS - Testes de Transição de Status")
class OrderServicoServiceTeste {

    @Test
    @DisplayName("ABERTA pode mudar para RECEBIDA")
    void devePermitirTransicaoDeAbertaParaRecebida() {
        assertDoesNotThrow(() -> StatusOS.ABERTA.podeMudarPara(StatusOS.RECEBIDA));
    }

    @Test
    @DisplayName("RECEBIDA pode mudar para EM_DIAGNOSTICO")
    void devePermitirTransicaoDeRecebidaParaDiagnostico() {
        assertDoesNotThrow(() -> StatusOS.RECEBIDA.podeMudarPara(StatusOS.EM_DIAGNOSTICO));
    }

    @Test
    @DisplayName("EM_DIAGNOSTICO pode mudar para AGUARDANDO_APROVACAO")
    void devePermitirTransicaoDeDiagnosticoParaAguardandoAprovacao() {
        assertDoesNotThrow(() -> StatusOS.EM_DIAGNOSTICO.podeMudarPara(StatusOS.AGUARDANDO_APROVACAO));
    }

    @Test
    @DisplayName("AGUARDANDO_APROVACAO pode mudar para APROVADA")
    void devePermitirTransicaoDeAguardandoAprovacaoParaAprovada() {
        assertDoesNotThrow(() -> StatusOS.AGUARDANDO_APROVACAO.podeMudarPara(StatusOS.APROVADA));
    }

    @Test
    @DisplayName("APROVADA pode mudar para EM_EXECUCAO")
    void devePermitirTransicaoDeAprovadaParaEmExecucao() {
        assertDoesNotThrow(() -> StatusOS.APROVADA.podeMudarPara(StatusOS.EM_EXECUCAO));
    }

    @Test
    @DisplayName("FINALIZADA pode mudar para ENTREGUE")
    void devePermitirTransicaoDeFinaliadaParaEntregue() {
        assertDoesNotThrow(() -> StatusOS.FINALIZADA.podeMudarPara(StatusOS.ENTREGUE));
    }

    @Test
    @DisplayName("ENTREGUE não pode mudar para nenhum status")
    void naoDevePermitirEntregueParaOutroStatus() {
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.ENTREGUE.podeMudarPara(StatusOS.RECEBIDA));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.ENTREGUE.podeMudarPara(StatusOS.EM_DIAGNOSTICO));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.ENTREGUE.podeMudarPara(StatusOS.CANCELADA));
    }

    @Test
    @DisplayName("EM_EXECUCAO não pode mudar para nenhum status diferente dos permitidos")
    void naoDevePermitirEmExecucaoParaNenhumOutroStatus() {
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.EM_EXECUCAO.podeMudarPara(StatusOS.RECEBIDA));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.EM_EXECUCAO.podeMudarPara(StatusOS.APROVADA));
    }

    @Test
    @DisplayName("CANCELADA não pode mudar para nenhum status")
    void naoDevePermitirCanceladaParaOutroStatus() {
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.CANCELADA.podeMudarPara(StatusOS.RECEBIDA));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.CANCELADA.podeMudarPara(StatusOS.APROVADA));
    }

    @Test
    @DisplayName("Permite CANCELADA de qualquer status (exceto se já é CANCELADA/ENTREGUE/FINALIZADA)")
    void devePermitirCancelarDeQualquerStatusValido() {
        assertDoesNotThrow(() -> StatusOS.ABERTA.podeMudarPara(StatusOS.CANCELADA));
        assertDoesNotThrow(() -> StatusOS.RECEBIDA.podeMudarPara(StatusOS.CANCELADA));
        assertDoesNotThrow(() -> StatusOS.EM_DIAGNOSTICO.podeMudarPara(StatusOS.CANCELADA));
        assertDoesNotThrow(() -> StatusOS.AGUARDANDO_APROVACAO.podeMudarPara(StatusOS.CANCELADA));
        assertDoesNotThrow(() -> StatusOS.APROVADA.podeMudarPara(StatusOS.CANCELADA));
    }

    @Test
    @DisplayName("Permite FINALIZADA de qualquer status (exceto se já é CANCELADA/ENTREGUE/FINALIZADA)")
    void devePermitirFinalizarDeQualquerStatusValido() {
        assertDoesNotThrow(() -> StatusOS.ABERTA.podeMudarPara(StatusOS.FINALIZADA));
        assertDoesNotThrow(() -> StatusOS.RECEBIDA.podeMudarPara(StatusOS.FINALIZADA));
        assertDoesNotThrow(() -> StatusOS.EM_DIAGNOSTICO.podeMudarPara(StatusOS.FINALIZADA));
    }

    @Test
    @DisplayName("Permite ENTREGUE de qualquer status (exceto se já é CANCELADA/ENTREGUE/FINALIZADA)")
    void devePermitirEntregarDeQualquerStatusValido() {
        assertDoesNotThrow(() -> StatusOS.ABERTA.podeMudarPara(StatusOS.ENTREGUE));
        assertDoesNotThrow(() -> StatusOS.RECEBIDA.podeMudarPara(StatusOS.ENTREGUE));
        assertDoesNotThrow(() -> StatusOS.APROVADA.podeMudarPara(StatusOS.ENTREGUE));
    }

    @Test
    @DisplayName("Não permite mudar de CANCELADA para nenhum status")
    void naoDevePermitirMudarDeCanceladaPara() {
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.CANCELADA.podeMudarPara(StatusOS.RECEBIDA));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.CANCELADA.podeMudarPara(StatusOS.EM_DIAGNOSTICO));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.CANCELADA.podeMudarPara(StatusOS.CANCELADA));
    }

    @Test
    @DisplayName("Se está em ENTREGUE não pode mudar para nada")
    void naoDevePermitirMudarDeEntregue() {
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.ENTREGUE.podeMudarPara(StatusOS.CANCELADA));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.ENTREGUE.podeMudarPara(StatusOS.ENTREGUE));
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.ENTREGUE.podeMudarPara(StatusOS.FINALIZADA));
    }

    @Test
    @DisplayName("Se está em FINALIZADA não pode mudar para CANCELADA")
    void naoDevePermitirMudarDeFinaliadaParaCancelada() {
        assertThrows(StatusOSInvalidoException.class, () -> StatusOS.FINALIZADA.podeMudarPara(StatusOS.CANCELADA));
    }

    @Test
    @DisplayName("Transição inválida lança exceção com mensagem correta")
    void deveCapturarMensagemDeErroComTransicaoInvalida() {
        StatusOSInvalidoException exception = assertThrows(
                StatusOSInvalidoException.class,
                () -> StatusOS.ABERTA.podeMudarPara(StatusOS.APROVADA)
        );
        assertTrue(exception.getMessage().contains("Transição de status inválida"));
        assertTrue(exception.getMessage().contains("ABERTA"));
        assertTrue(exception.getMessage().contains("APROVADA"));
    }
}

