-- ========================================
-- Migration: V10__inserir_dados_servicos
-- Descrição: Insere serviços de exemplo para testes
-- Data: 2026-04-28
-- Autor: Auto Center FIAP Team
-- ========================================

INSERT INTO servicos (descricao, status, valor, data_criacao, data_ultima_atualizacao)
VALUES 
('Troca de Óleo e Filtro', 'ATIVO', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alinhamento e Balanceamento', 'ATIVO', 120.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Revisão Geral', 'ATIVO', 350.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Troca de Pastilhas de Freio', 'ATIVO', 200.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Higienização de Ar Condicionado', 'ATIVO', 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Limpeza de Bicos Injetores', 'ATIVO', 180.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
