package br.com.autocenterfiap.ordemservico.domain.enums;

import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;

public enum StatusOS {
    ABERTA,
    RECEBIDA,
    EM_DIAGNOSTICO,
    AGUARDANDO_APROVACAO,
    APROVADA,
    EM_EXECUCAO,
    FINALIZADA,
    ENTREGUE,
    CANCELADA;

    public void podeMudarPara(StatusOS novoStatus) {

        if (this != CANCELADA && this != ENTREGUE && this != FINALIZADA) {
            if (novoStatus == CANCELADA) return;
            if (novoStatus == FINALIZADA) return;
            if (novoStatus == ENTREGUE) return;
        }

        boolean transicaoValida = switch (this) {
            case ABERTA -> novoStatus == RECEBIDA;
            case RECEBIDA -> novoStatus == EM_DIAGNOSTICO;
            case EM_DIAGNOSTICO -> novoStatus == AGUARDANDO_APROVACAO;
            case AGUARDANDO_APROVACAO -> novoStatus == APROVADA;
            case APROVADA -> novoStatus == EM_EXECUCAO;
            case FINALIZADA -> novoStatus == ENTREGUE;
            case ENTREGUE,EM_EXECUCAO, CANCELADA -> false;
        };

        if(!transicaoValida){
            throw new StatusOSInvalidoException("Transição de status inválida: " + this + " para " + novoStatus);
        }
    }
}
