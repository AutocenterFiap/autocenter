-- ========================================
-- Migration: V6__criar_tabela_pecas
-- Descrição: Cria tabelas de peças/insumos e itens de peça na OS
-- Data: 2026-04-25
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE pecas (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome                  VARCHAR(255)   NOT NULL,
    codigo                VARCHAR(100)   NOT NULL UNIQUE,
    descricao             VARCHAR(1000),
    unidade_medida        VARCHAR(10)    NOT NULL,
    preco_unitario        NUMERIC(15, 2) NOT NULL,
    quantidade_estoque    INT            NOT NULL,
    estoque_minimo        INT            NOT NULL,
    categoria             VARCHAR(100)   NOT NULL,
    tipo                  VARCHAR(10)    NOT NULL,
    ativo                 BOOLEAN        NOT NULL DEFAULT TRUE,
    data_criacao          TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP
);

CREATE INDEX idx_pecas_codigo    ON pecas (codigo);
CREATE INDEX idx_pecas_categoria ON pecas (categoria);
CREATE INDEX idx_pecas_ativo     ON pecas (ativo);

-- Tabela de itens de peça vinculados a uma Ordem de Serviço
-- NOTA: a FK para ordem_servico será adicionada quando essa tabela existir
CREATE TABLE os_item_peca (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id          BIGINT         NOT NULL,
    peca_id                   BIGINT         NOT NULL,
    quantidade                INT            NOT NULL,
    preco_unitario_no_momento NUMERIC(15, 2) NOT NULL,
    data_criacao              TIMESTAMP,
    data_ultima_atualizacao   TIMESTAMP,
    CONSTRAINT fk_os_item_peca_peca FOREIGN KEY (peca_id) REFERENCES pecas (id)
);

CREATE INDEX idx_os_item_peca_os   ON os_item_peca (ordem_servico_id);
CREATE INDEX idx_os_item_peca_peca ON os_item_peca (peca_id);
