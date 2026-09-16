-- ========================================
-- Migration: V13__adiciona_status_cliente
-- Descrição: Adiciona a coluna de status do cliente (usada pela autenticação
--            serverless por CPF) e corrige CPFs de exemplo inválidos.
-- Data: 2026-08-15
-- Autor: Auto Center FIAP Team
-- ========================================

-- Coluna de status (ATIVO | INATIVO | BLOQUEADO). Apenas ATIVO pode autenticar.
ALTER TABLE clientes ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO';

CREATE INDEX idx_status ON clientes(status);

-- ----------------------------------------
-- Correção dos CPFs de exemplo (V2 inseriu documentos com dígito verificador
-- inválido, o que impediria a autenticação via CPF). Substitui por CPFs válidos.
-- ----------------------------------------
UPDATE clientes SET documento = '11144477735' WHERE documento = '12345678901'; -- João da Silva
UPDATE clientes SET documento = '22255588846' WHERE documento = '98765432109'; -- Maria Santos
UPDATE clientes SET documento = '33366699957' WHERE documento = '11122233344'; -- Pedro Oliveira

-- Cliente inativo para demonstrar o bloqueio de autenticação (HTTP 403).
UPDATE clientes SET status = 'INATIVO' WHERE documento = '33366699957';
