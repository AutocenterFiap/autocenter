-- ========================================
-- Migration: V5__criar_tabela_servicos
-- Descrição: Cria a tabela de servicos mecânicos
-- Data: 2026-04-18
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE servicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_descricao ON servicos(descricao);
CREATE INDEX idx_status ON servicos(status);