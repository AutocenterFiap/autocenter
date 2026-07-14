package br.com.autocenterfiap.servico.domain.exception;

public class ServicoInativoException extends DomainException {
    public ServicoInativoException(String descricao) {
        super(String.format("Serviço '%s' está inativo e não pode ser adicionado à ordem de serviço", descricao));
    }
}
