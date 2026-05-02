-- =========================
-- OS 1003 (id = 3)
-- =========================

-- Troca de Óleo (~1h10min)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     3, 1, 150.00, 'FINALIZADO',
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -4 HOUR),
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -170 MINUTE), -- 2h50 atrás
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Alinhamento (~40min)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     3, 2, 120.00, 'FINALIZADO',
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -3 HOUR),
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -140 MINUTE),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Revisão (~2h)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     3, 3, 350.00, 'FINALIZADO',
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -6 HOUR),
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -4 HOUR),
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
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -2 HOUR),
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -70 MINUTE),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Alinhamento (~1h05)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     4, 2, 120.00, 'FINALIZADO',
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -2 HOUR),
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -55 MINUTE),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );

-- Revisão (~2h30)
INSERT INTO os_item_servico (
    ordem_servico_id, servico_id, valor_item_servico, status_servico,
    data_hora_inicio, data_hora_fim, data_criacao, data_ultima_atualizacao
) VALUES (
     4, 3, 350.00, 'FINALIZADO',
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -5 HOUR),
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -150 MINUTE),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
 );