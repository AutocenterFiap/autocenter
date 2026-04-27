package br.com.autocenterfiap.peca.exception;

public class OSItemPecaNaoEncontradoException extends RuntimeException {

    public OSItemPecaNaoEncontradoException(Long ordemServicoId, Long pecaId) {
        super(String.format("Peça ID %d não encontrada na Ordem de Serviço ID %d", pecaId, ordemServicoId));
    }
}
