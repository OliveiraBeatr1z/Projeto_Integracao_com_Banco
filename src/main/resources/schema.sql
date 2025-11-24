-- Criação da tabela de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL
);

-- Criação da tabela de contas
CREATE TABLE IF NOT EXISTS conta (
    numero INT PRIMARY KEY,
    saldo DECIMAL(19, 2) NOT NULL,
    cliente_id BIGINT NOT NULL,
    esta_ativa BOOLEAN NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Criação da tabela de histórico de transações
CREATE TABLE IF NOT EXISTS historico_transacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_conta INT NOT NULL,
    tipo_operacao VARCHAR(255) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    saldo_anterior DECIMAL(19, 2),
    saldo_novo DECIMAL(19, 2),
    data_hora DATETIME NOT NULL,
    descricao VARCHAR(500)
);
