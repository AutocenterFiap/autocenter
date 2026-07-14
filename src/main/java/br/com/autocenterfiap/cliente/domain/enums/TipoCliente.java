package br.com.autocenterfiap.cliente.domain.enums;

public enum TipoCliente {
    PESSOA_FISICA("Pessoa Física", 11),
    PESSOA_JURIDICA("Pessoa Jurídica", 14);

    private final String descricao;
    private final int tamanhoDocumento;

    TipoCliente(String descricao, int tamanhoDocumento) {
        this.descricao = descricao;
        this.tamanhoDocumento = tamanhoDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getTamanhoDocumento() {
        return tamanhoDocumento;
    }
}

