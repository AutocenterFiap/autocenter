-- ========================================
-- Migration: V5__cria_insere_dados_usuarios
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

INSERT INTO perfil (nome) VALUES ('ADMIN');
INSERT INTO perfil (nome) VALUES ('READ');
INSERT INTO perfil (nome) VALUES ('WRITE');

INSERT INTO usuario (nome, senha) VALUES ('ADMIN', '$2a$10$JLzN0RAohZTkZZpK1YH8g.zBrZTrN2Q5QbNK2b210gK1wlkg/DJA2');

INSERT INTO R_usuario_perfil (usuario_id, perfil_id)
VALUES (select id from usuario where nome = 'ADMIN',
           select id from perfil where nome = 'ADMIN');