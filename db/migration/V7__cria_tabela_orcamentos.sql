-- ========================================
-- Migration: V8__cria_orcamento
-- Descrição: Criação da tabela de orçamento
-- Data: 2026-04-25
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE orcamento (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    valor_total NUMERIC(15,2),
    status_orcamento VARCHAR(50),
    data_criacao TIMESTAMP NOT NULL,
    data_ultima_atualizacao TIMESTAMP,
    CONSTRAINT fk_orcamento_ordem_servico FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
);
