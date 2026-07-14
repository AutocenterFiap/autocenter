package br.com.autocenterfiap.ordemservico.application.exception;

public class OSItemServicoNaoEncontradoException extends RuntimeException {

    public OSItemServicoNaoEncontradoException(Long ordemServicoId, Long servicoId) {
        super(String.format("Item de serviço não encontrado para Ordem de Serviço ID=%d e Serviço ID=%d",
                ordemServicoId, servicoId));
    }

    public OSItemServicoNaoEncontradoException(Long id) {
        super(String.format("Item de serviço não encontrado com ID=%d", id));
    }
}
