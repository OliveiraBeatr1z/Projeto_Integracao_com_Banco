# 🚀 Guia Rápido - Front-End ByteBank

## Passo 1: Executar o Backend

```bash
cd /Users/beatriz.silva/Documents/faculdade/Lab\ BD/Projeto_Integracao_com_Banco
mvn spring-boot:run
```

## Passo 2: Acessar a Interface

Abra seu navegador e acesse:

```
http://localhost:8080
```

## Passo 3: Explorar as Funcionalidades

### 🏠 Tela Inicial
- Visualize os cards com acesso rápido
- Clique em qualquer card para ir para a seção

### 💳 Seção Contas
1. Clique em **"Nova Conta"**
2. Preencha os dados:
   - Número da conta (ex: 1001)
   - Nome do cliente
   - CPF (11 dígitos)
   - Email
3. Clique em **"Criar Conta"**
4. Veja a conta aparecer na lista

### 💰 Seção Operações

**Depósito:**
- Número da conta: 1001
- Valor: 5000
- Clique em "Depositar"

**Saque:**
- Número da conta: 1001
- Valor: 500
- Clique em "Sacar"

**Transferência:**
- Conta Origem: 1001
- Conta Destino: 1002
- Valor: 1000
- Clique em "Transferir"

**Consultar Saldo:**
- Número da conta: 1001
- Clique em "Consultar"

### 📜 Seção Histórico
1. Digite o número da conta: 1001
2. (Opcional) Escolha período:
   - Data Início: 2024-11-01
   - Data Fim: 2024-11-30
3. Clique em **"Consultar Extrato"**
4. Visualize todas as transações com:
   - Tipo de operação (com ícone e cor)
   - Valor
   - Saldo anterior → Saldo novo
   - Data e hora

### 📊 Seção Relatórios

**Saldo Total do Banco:**
- Clique em "Saldo Total do Banco"
- Veja estatísticas:
  - Total de contas
  - Saldo total
  - Saldo médio
  - Maior e menor saldo

**Contas com Saldo Baixo:**
- Clique em "Contas com Saldo Baixo"
- Digite o limite (ex: 1000)
- Veja lista de contas abaixo do limite

**Relatório de Movimentações:**
- Clique em "Relatório de Movimentações"
- Escolha o período
- Veja movimentações por tipo

## 🎯 Fluxo Completo de Teste

```bash
# 1. Criar duas contas
Conta 1: 1001 - João Silva - 12345678901
Conta 2: 1002 - Maria Santos - 98765432100

# 2. Fazer depósitos
Depositar R$ 5.000 na conta 1001
Depositar R$ 3.000 na conta 1002

# 3. Fazer saque
Sacar R$ 500 da conta 1001

# 4. Fazer transferência
Transferir R$ 1.000 da conta 1001 para 1002

# 5. Ver histórico
Consultar extrato da conta 1001
Deve mostrar: ABERTURA, DEPOSITO, SAQUE, TRANSFERENCIA_ENVIADA

# 6. Ver relatórios
Clicar em "Saldo Total do Banco"
Ver estatísticas gerais
```

## ✨ Recursos da Interface

### Notificações Toast
Aparecem no canto inferior direito:
- ✅ Verde: Sucesso
- ❌ Vermelho: Erro
- ⚠️ Laranja: Aviso
- ℹ️ Azul: Informação

### Navegação
Use o menu superior para navegar:
- 🏠 Início
- 💳 Contas
- 💰 Operações
- 📜 Histórico
- 📊 Relatórios

### Design Responsivo
Funciona perfeitamente em:
- 📱 Smartphone
- 💻 Tablet
- 🖥️ Desktop

## 🐛 Solução de Problemas

### Página não carrega
```bash
# Verifique se o backend está rodando
curl http://localhost:8080/api/contas
```

### "Failed to fetch"
```bash
# O backend não está rodando
# Execute: mvn spring-boot:run
```

### CORS Error
```bash
# Já configurado! Se aparecer, reinicie o backend
```

### Botões não funcionam
```bash
# Abra F12 (DevTools) e veja o console
# Verifique se há erros JavaScript
```

## 📸 Preview das Telas

### Tela Inicial
- Design moderno com gradiente
- 4 cards principais
- Navegação intuitiva

### Telas de Operação
- Formulários limpos
- Validação em tempo real
- Feedback imediato

### Histórico
- Timeline de transações
- Cores por tipo de operação
- Filtros por período

### Relatórios
- Estatísticas visuais
- Tabelas organizadas
- Dados das procedures

## 🎨 Personalização

### Mudar Cores
Edite `src/main/resources/static/styles.css`:

```css
:root {
    --primary-color: #4A90E2;    /* Sua cor */
    --secondary-color: #50E3C2;  /* Sua cor */
}
```

### Mudar URL da API
Edite `src/main/resources/static/script.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## 🎓 Demonstre seus Conhecimentos

Esta interface demonstra:
- ✅ Consumo de API REST
- ✅ JavaScript assíncrono (async/await)
- ✅ Manipulação do DOM
- ✅ CSS Grid e Flexbox
- ✅ Responsividade
- ✅ Tratamento de erros
- ✅ UX/UI moderno

---

**Projeto pronto para apresentação! 🎉**

