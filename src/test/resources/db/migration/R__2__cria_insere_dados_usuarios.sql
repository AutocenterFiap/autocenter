-- ========================================
-- Migration: V2__cria_insere_dados_usuarios
-- Descrição: Insere usuarios
-- Data: 2026-04-11
-- Autor: Auto Center FIAP Team
-- ========================================

CREATE TABLE perfil (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);
CREATE INDEX idx_perfil_nome ON perfil(nome);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL
);

-- Criar índices para melhor performance
CREATE INDEX idx_usuario_nome ON usuario(nome);

CREATE TABLE r_usuario_perfil (
    usuario_id BIGINT,
    perfil_id BIGINT);
