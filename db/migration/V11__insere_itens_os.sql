-- =========================
-- OS 1003 (id = 3)
-- =========================

-- Troca de Óleo (~1h10min)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     3, 1, 150.00, 'FINALIZADO',
     DATEADD('HOUR', -4, CURRENT_TIMESTAMP),
     DATEADD('MINUTE', -170, CURRENT_TIMESTAMP), -- 2h50 atrás
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Alinhamento (~40min)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     3, 2, 120.00, 'FINALIZADO',
     DATEADD('HOUR', -3, CURRENT_TIMESTAMP),
     DATEADD('MINUTE', -140, CURRENT_TIMESTAMP),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Revisão (~2h)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     3, 3, 350.00, 'FINALIZADO',
     DATEADD('HOUR', -6, CURRENT_TIMESTAMP),
     DATEADD('HOUR', -4, CURRENT_TIMESTAMP),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- =========================
-- OS 1004 (id = 4)
-- =========================

-- Troca de Óleo (~50min)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     4, 1, 150.00, 'FINALIZADO',
     DATEADD('HOUR', -2, CURRENT_TIMESTAMP),
     DATEADD('MINUTE', -70, CURRENT_TIMESTAMP),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Alinhamento (~1h05)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     4, 2, 120.00, 'FINALIZADO',
     DATEADD('HOUR', -2, CURRENT_TIMESTAMP),
     DATEADD('MINUTE', -55, CURRENT_TIMESTAMP),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Revisão (~2h30)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     4, 3, 350.00, 'FINALIZADO',
     DATEADD('HOUR', -5, CURRENT_TIMESTAMP),
     DATEADD('MINUTE', -150, CURRENT_TIMESTAMP),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );