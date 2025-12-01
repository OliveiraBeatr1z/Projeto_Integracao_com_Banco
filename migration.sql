-- Script de migração do schema antigo para o novo schema com JPA/Hibernate
-- Execute este script APENAS se você já possui um banco de dados com dados existentes
-- Se for um banco novo, o Hibernate criará tudo automaticamente

-- 1. Criar tabela de cliente separada (se não existir)
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL
);

-- 2. Se você tem uma tabela conta antiga com estrutura diferente, faça backup primeiro
-- CREATE TABLE conta_backup AS SELECT * FROM conta;

-- 3. Migrar dados de cliente da tabela conta para a tabela cliente (se necessário)
-- INSERT IGNORE INTO cliente (nome, cpf, email)
-- SELECT DISTINCT cliente_nome, cliente_cpf, cliente_email 
-- FROM conta 
-- WHERE cliente_cpf IS NOT NULL;

-- 4. Adicionar coluna cliente_id na tabela conta (se não existir)
-- ALTER TABLE conta ADD COLUMN IF NOT EXISTS cliente_id BIGINT;

-- 5. Atualizar cliente_id com base no cpf
-- UPDATE conta c 
-- SET c.cliente_id = (
--     SELECT cl.id FROM cliente cl WHERE cl.cpf = c.cliente_cpf
-- );

-- 6. Adicionar coluna id como PRIMARY KEY auto-increment (nova estrutura JPA)
-- Se a tabela já existe, você pode precisar recriar ou adicionar a coluna id
-- ALTER TABLE conta ADD COLUMN IF NOT EXISTS id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST;

-- 7. Remover colunas antigas de cliente da tabela conta (após migração)
-- ALTER TABLE conta DROP COLUMN IF EXISTS cliente_nome;
-- ALTER TABLE conta DROP COLUMN IF EXISTS cliente_cpf;
-- ALTER TABLE conta DROP COLUMN IF EXISTS cliente_email;

-- 8. Adicionar foreign key constraint
-- ALTER TABLE conta 
-- ADD CONSTRAINT fk_conta_cliente 
-- FOREIGN KEY (cliente_id) REFERENCES cliente(id);

-- 9. Adicionar índices para performance
CREATE INDEX IF NOT EXISTS idx_conta_numero ON conta(numero);
CREATE INDEX IF NOT EXISTS idx_conta_esta_ativa ON conta(esta_ativa);
CREATE INDEX IF NOT EXISTS idx_cliente_cpf ON cliente(cpf);

-- =============================================================================
-- IMPORTANTE: Se você está começando do zero, simplesmente delete o banco e deixe
-- o Hibernate criar tudo automaticamente:
--
-- DROP DATABASE IF EXISTS bytebank;
-- CREATE DATABASE bytebank;
--
-- E então inicie a aplicação Spring Boot
-- =============================================================================

-- Para verificar a estrutura criada pelo Hibernate, use:
-- DESCRIBE conta;
-- DESCRIBE cliente;
-- SHOW CREATE TABLE conta;
-- SHOW CREATE TABLE cliente;

