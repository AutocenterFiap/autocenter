-- ========================================
-- Migration: V12__inserir_dados_produtos_apresentacao
-- Descricao: Insere produtos de exemplo para apresentacao
-- Data: 2026-05-05
-- Autor: Auto Center FIAP Team
-- ========================================

INSERT INTO produtos (
    nome,
    codigo,
    descricao,
    unidade_medida,
    preco_unitario,
    quantidade_estoque,
    estoque_minimo,
    categoria,
    tipo,
    ativo,
    data_criacao,
    data_ultima_atualizacao
) VALUES
('Filtro de Oleo', 'PROD-001', 'Filtro de oleo para motores 1.0 a 2.0', 'UNIT', 45.90, 30, 8, 'Motor', 'PECAS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Oleo de Motor 5W30', 'PROD-002', 'Oleo sintetico 5W30 frasco 1L', 'LITER', 39.90, 120, 30, 'Lubrificantes', 'INSUMOS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pastilha de Freio Dianteira', 'PROD-003', 'Jogo de pastilhas dianteiras', 'BOX', 189.00, 18, 5, 'Freios', 'PECAS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Disco de Freio Ventilado', 'PROD-004', 'Disco ventilado aro 15', 'UNIT', 249.90, 12, 4, 'Freios', 'PECAS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Aditivo para Radiador', 'PROD-005', 'Aditivo concentrado para sistema de arrefecimento', 'LITER', 29.90, 45, 10, 'Arrefecimento', 'INSUMOS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Filtro de Ar do Motor', 'PROD-006', 'Elemento filtrante para admissao de ar', 'UNIT', 59.90, 25, 7, 'Motor', 'PECAS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Correia Dentada', 'PROD-007', 'Correia dentada para kit de distribuicao', 'UNIT', 129.90, 20, 6, 'Motor', 'PECAS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Limpa Contato Eletrico', 'PROD-008', 'Spray limpa contato para conectores eletricos', 'UNIT', 24.90, 40, 12, 'Eletrica', 'INSUMOS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

