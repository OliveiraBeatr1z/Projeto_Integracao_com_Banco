# 📊 Documentação: Procedures e Triggers do ByteBank

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Tabela de Histórico](#tabela-de-histórico)
3. [Triggers](#triggers)
4. [Procedures](#procedures)
5. [Endpoints da API](#endpoints-da-api)
6. [Exemplos de Uso](#exemplos-de-uso)

---

## 🎯 Visão Geral

Este projeto inclui **2 Triggers** e **5 Stored Procedures** que automatizam operações e fornecem relatórios avançados.

### **Por que Triggers e Procedures?**

- ✅ **Automatização**: Triggers registram transações automaticamente
- ✅ **Performance**: Procedures executam operações complexas no banco
- ✅ **Auditoria**: Histórico completo de todas as operações
- ✅ **Relatórios**: Análises avançadas com SQL otimizado

---

## 📊 Tabela de Histórico

### `historico_transacao`

Armazena automaticamente todas as transações realizadas.

```sql
CREATE TABLE historico_transacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_conta INTEGER NOT NULL,
    tipo_operacao VARCHAR(50) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    saldo_anterior DECIMAL(19,2) NOT NULL,
    saldo_novo DECIMAL(19,2) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descricao VARCHAR(255)
);
```

**Tipos de Operação:**
- `ABERTURA` - Abertura de conta
- `DEPOSITO` - Depósito realizado
- `SAQUE` - Saque realizado
- `TRANSFERENCIA_ENVIADA` - Transferência enviada
- `TRANSFERENCIA_RECEBIDA` - Transferência recebida
- `TAXA_MANUTENCAO` - Taxa de manutenção cobrada

---

## ⚡ Triggers

### 1. `trg_registrar_transacao`

**Quando dispara:** AFTER UPDATE na tabela `conta`  
**Função:** Registra automaticamente depósitos e saques no histórico

```sql
-- Exemplo: Ao fazer um depósito, o trigger registra automaticamente
UPDATE conta SET saldo = saldo + 100 WHERE numero = 1001;
-- ✅ Trigger cria registro em historico_transacao
```

**Como funciona:**
1. Detecta mudança no saldo
2. Identifica se foi depósito (saldo aumentou) ou saque (saldo diminuiu)
3. Calcula o valor da operação
4. Insere registro no histórico com todos os dados

### 2. `trg_registrar_abertura_conta`

**Quando dispara:** AFTER INSERT na tabela `conta`  
**Função:** Registra a abertura de uma nova conta

```sql
-- Exemplo: Ao criar conta, o trigger registra automaticamente
INSERT INTO conta (numero, saldo, cliente_id, esta_ativa) 
VALUES (1001, 0, 1, TRUE);
-- ✅ Trigger cria registro de ABERTURA no histórico
```

---

## 🔧 Procedures (Stored Procedures)

### 1. `sp_extrato_conta`

**Descrição:** Consulta o extrato de uma conta em um período

**Parâmetros:**
- `p_numero_conta` - Número da conta
- `p_data_inicio` - Data inicial (pode ser NULL)
- `p_data_fim` - Data final (pode ser NULL)

**Exemplo SQL:**
```sql
-- Extrato completo
CALL sp_extrato_conta(1001, NULL, NULL);

-- Extrato do último mês
CALL sp_extrato_conta(1001, '2024-11-01', '2024-11-30');
```

**Uso via API:**
```bash
# Extrato completo
GET http://localhost:8080/api/historico/extrato/1001

# Extrato por período
GET http://localhost:8080/api/historico/extrato/1001?dataInicio=2024-11-01&dataFim=2024-11-30
```

---

### 2. `sp_saldo_total_banco`

**Descrição:** Calcula estatísticas de saldo de todas as contas ativas

**Retorna:**
- Total de contas
- Saldo total
- Saldo médio
- Maior saldo
- Menor saldo

**Exemplo SQL:**
```sql
CALL sp_saldo_total_banco();
```

**Resultado:**
```
+-------------+-------------+-------------+-------------+-------------+
| total_contas| saldo_total | saldo_medio | maior_saldo | menor_saldo |
+-------------+-------------+-------------+-------------+-------------+
| 10          | 50000.00    | 5000.00     | 15000.00    | 100.00      |
+-------------+-------------+-------------+-------------+-------------+
```

**Uso via API:**
```bash
GET http://localhost:8080/api/historico/relatorio/saldo-total
```

---

### 3. `sp_contas_saldo_baixo`

**Descrição:** Lista contas com saldo abaixo de um limite

**Parâmetros:**
- `p_limite_saldo` - Valor limite para filtrar

**Exemplo SQL:**
```sql
-- Contas com saldo abaixo de R$ 100
CALL sp_contas_saldo_baixo(100.00);
```

**Uso via API:**
```bash
# Saldo abaixo de R$ 100 (padrão)
GET http://localhost:8080/api/historico/relatorio/contas-saldo-baixo

# Saldo abaixo de R$ 500
GET http://localhost:8080/api/historico/relatorio/contas-saldo-baixo?limiteSaldo=500.00
```

---

### 4. `sp_aplicar_taxa_manutencao`

**Descrição:** Aplica taxa de manutenção em todas as contas ativas

**Parâmetros:**
- `p_valor_taxa` - Valor da taxa a ser cobrada

**Retorna:**
- Número de contas afetadas
- Valor da taxa
- Total arrecadado

**Exemplo SQL:**
```sql
-- Aplicar taxa de R$ 10 em todas as contas
CALL sp_aplicar_taxa_manutencao(10.00);
```

**⚠️ ATENÇÃO:** Esta procedure modifica dados! Use com cuidado.

**Uso via API:**
```bash
POST http://localhost:8080/api/historico/admin/aplicar-taxa
Content-Type: application/json

{
  "valorTaxa": 10.00
}
```

---

### 5. `sp_relatorio_movimentacoes`

**Descrição:** Relatório de movimentações por tipo em um período

**Parâmetros:**
- `p_data_inicio` - Data inicial
- `p_data_fim` - Data final

**Retorna:**
- Tipo de operação
- Quantidade de operações
- Valor total
- Valor médio
- Maior valor
- Menor valor

**Exemplo SQL:**
```sql
-- Movimentações de novembro de 2024
CALL sp_relatorio_movimentacoes('2024-11-01', '2024-11-30');
```

**Uso via API:**
```bash
GET http://localhost:8080/api/historico/relatorio/movimentacoes?dataInicio=2024-11-01&dataFim=2024-11-30
```

---

### 6. `sp_transferencia` (Bônus)

**Descrição:** Realiza transferência com validação completa

**Parâmetros:**
- `p_conta_origem` - Número da conta de origem
- `p_conta_destino` - Número da conta de destino
- `p_valor` - Valor da transferência
- `p_mensagem` (OUT) - Mensagem de retorno
- `p_sucesso` (OUT) - Indica sucesso ou falha

**Validações:**
- ✅ Verifica se ambas as contas existem
- ✅ Verifica se ambas estão ativas
- ✅ Verifica saldo suficiente
- ✅ Verifica se valor é positivo
- ✅ Usa transação (ROLLBACK em caso de erro)

**Exemplo SQL:**
```sql
CALL sp_transferencia(1001, 1002, 500.00, @msg, @sucesso);
SELECT @msg as mensagem, @sucesso as sucesso;
```

---

## 🌐 Endpoints da API

### Consultar Histórico

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/historico/conta/{numero}` | Histórico completo da conta |
| GET | `/api/historico/conta/{numero}/periodo` | Histórico por período |
| GET | `/api/historico/tipo/{tipo}` | Histórico por tipo de operação |

### Usar Procedures

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/historico/extrato/{numero}` | Extrato via procedure |
| GET | `/api/historico/relatorio/saldo-total` | Estatísticas gerais |
| GET | `/api/historico/relatorio/contas-saldo-baixo` | Contas com saldo baixo |
| GET | `/api/historico/relatorio/movimentacoes` | Relatório de movimentações |
| POST | `/api/historico/admin/aplicar-taxa` | Aplicar taxa de manutenção |

---

## 🚀 Exemplos de Uso Completos

### 1. Criar conta e verificar histórico

```bash
# 1. Criar conta
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{
    "numero": 1001,
    "dadosCliente": {
      "nome": "João Silva",
      "cpf": "12345678901",
      "email": "joao@email.com"
    }
  }'

# 2. Ver histórico (deve mostrar ABERTURA)
curl http://localhost:8080/api/historico/conta/1001
```

### 2. Fazer depósito e verificar trigger

```bash
# 1. Fazer depósito
curl -X POST http://localhost:8080/api/contas/1001/deposito \
  -H "Content-Type: application/json" \
  -d '{"valor": 1000.00}'

# 2. Ver histórico (deve mostrar ABERTURA + DEPOSITO)
curl http://localhost:8080/api/historico/conta/1001
```

### 3. Consultar extrato via procedure

```bash
# Extrato completo
curl http://localhost:8080/api/historico/extrato/1001

# Extrato de novembro
curl "http://localhost:8080/api/historico/extrato/1001?dataInicio=2024-11-01&dataFim=2024-11-30"
```

### 4. Gerar relatório de saldo total

```bash
curl http://localhost:8080/api/historico/relatorio/saldo-total
```

**Resposta:**
```json
{
  "totalContas": 5,
  "saldoTotal": 15000.00,
  "saldoMedio": 3000.00,
  "maiorSaldo": 8000.00,
  "menorSaldo": 500.00
}
```

### 5. Listar contas com saldo baixo

```bash
# Contas com menos de R$ 200
curl "http://localhost:8080/api/historico/relatorio/contas-saldo-baixo?limiteSaldo=200.00"
```

### 6. Aplicar taxa de manutenção (cuidado!)

```bash
curl -X POST http://localhost:8080/api/historico/admin/aplicar-taxa \
  -H "Content-Type: application/json" \
  -d '{"valorTaxa": 5.00}'
```

### 7. Relatório de movimentações do mês

```bash
curl "http://localhost:8080/api/historico/relatorio/movimentacoes?dataInicio=2024-11-01&dataFim=2024-11-30"
```

---

## 📝 Como Instalar

### 1. Execute o script SQL

```bash
# Via linha de comando
mysql -u root -p bytebank < src/main/resources/schema.sql

# Ou dentro do MySQL
USE bytebank;
SOURCE /caminho/completo/para/schema.sql;
```

### 2. Reinicie a aplicação

```bash
mvn spring-boot:run
```

### 3. Teste os endpoints

Use o arquivo `api-examples-procedures.http` (será criado a seguir).

---

## ✅ Verificar se foi instalado corretamente

```sql
-- Ver procedures criadas
SHOW PROCEDURE STATUS WHERE Db = 'bytebank';

-- Ver triggers criados
SHOW TRIGGERS FROM bytebank;

-- Testar uma procedure
CALL sp_saldo_total_banco();

-- Ver estrutura da tabela de histórico
DESCRIBE historico_transacao;
```

---

## 🎓 Conceitos Aprendidos

- ✅ **Triggers**: Automatização de ações no banco de dados
- ✅ **Stored Procedures**: Lógica de negócio no banco
- ✅ **Auditoria**: Registro de todas as operações
- ✅ **Transações**: COMMIT e ROLLBACK
- ✅ **Parâmetros OUT**: Retorno de múltiplos valores
- ✅ **Cursor e Loop**: Iteração sobre resultados
- ✅ **Agregações**: SUM, AVG, COUNT, etc.
- ✅ **Integração JPA**: Chamar procedures via Spring

---

## 🆘 Troubleshooting

### Erro: "Procedure does not exist"
→ Execute o script `schema.sql` novamente

### Triggers não disparam
→ Verifique se foram criados: `SHOW TRIGGERS FROM bytebank;`

### Histórico não aparece
→ Verifique se a tabela existe: `DESCRIBE historico_transacao;`

### Erro ao chamar procedure via API
→ Verifique logs da aplicação e sintaxe SQL

---

**Desenvolvido para demonstrar conceitos avançados de SQL** 🎯

