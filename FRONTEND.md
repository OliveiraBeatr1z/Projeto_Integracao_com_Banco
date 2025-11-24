# 🎨 Front-End ByteBank

Interface web moderna e responsiva para o sistema bancário ByteBank.

## 🌟 Características

- ✅ Design moderno e responsivo
- ✅ Interface intuitiva e fácil de usar
- ✅ Consumo completo da API REST
- ✅ Notificações toast para feedback
- ✅ Animações suaves
- ✅ Compatível com dispositivos móveis
- ✅ Sem dependências externas (JavaScript Vanilla)

## 📁 Estrutura dos Arquivos

```
src/main/resources/static/
├── index.html      # Página principal HTML
├── styles.css      # Estilos CSS
└── script.js       # JavaScript (API calls)
```

## 🎯 Funcionalidades Implementadas

### 1. **Gerenciar Contas**
- ✅ Listar todas as contas ativas
- ✅ Criar nova conta
- ✅ Visualizar detalhes (número, saldo, titular)
- ✅ Cards visuais para cada conta

### 2. **Operações Bancárias**
- ✅ Realizar depósito
- ✅ Realizar saque
- ✅ Realizar transferência entre contas
- ✅ Consultar saldo
- ✅ Encerrar conta

### 3. **Histórico de Transações**
- ✅ Consultar extrato completo
- ✅ Filtrar por período (data início/fim)
- ✅ Visualizar detalhes de cada transação
- ✅ Ícones e cores por tipo de operação

### 4. **Relatórios e Estatísticas**
- ✅ Saldo total do banco (usando stored procedure)
- ✅ Contas com saldo baixo
- ✅ Relatório de movimentações por período
- ✅ Estatísticas visuais

## 🚀 Como Usar

### 1. Executar a Aplicação Spring Boot

```bash
cd /Users/beatriz.silva/Documents/faculdade/Lab\ BD/Projeto_Integracao_com_Banco
mvn spring-boot:run
```

### 2. Acessar o Front-End

Abra o navegador e acesse:

```
http://localhost:8080
```

### 3. Explorar a Interface

#### **Tela Inicial (Home)**
- Cards com links rápidos para cada seção
- Design atrativo e moderno

#### **Seção Contas**
- Clique em "Nova Conta" para abrir o formulário
- Preencha os dados e clique em "Criar Conta"
- As contas aparecem em cards visuais

#### **Seção Operações**
- **Depósito**: Informe número da conta e valor
- **Saque**: Informe número da conta e valor
- **Transferência**: Informe origem, destino e valor
- **Consultar Saldo**: Informe número da conta
- **Encerrar Conta**: Informe número da conta (apenas se saldo = 0)

#### **Seção Histórico**
- Informe o número da conta
- Opcionalmente, defina um período (data início/fim)
- Clique em "Consultar Extrato"
- Visualize todas as transações com cores e ícones

#### **Seção Relatórios**
- **Saldo Total**: Estatísticas gerais do banco
- **Contas com Saldo Baixo**: Lista contas abaixo de um limite
- **Movimentações**: Relatório por tipo de operação e período

## 🎨 Design e Cores

### Paleta de Cores

```css
--primary-color: #4A90E2    /* Azul Principal */
--secondary-color: #50E3C2  /* Verde Água */
--success-color: #7ED321    /* Verde Sucesso */
--warning-color: #F5A623    /* Laranja Aviso */
--danger-color: #E74C3C     /* Vermelho Perigo */
--info-color: #3498DB       /* Azul Info */
```

### Ícones

Usa Font Awesome 6 para todos os ícones:
- 🏦 Banco (navbar)
- 💳 Contas
- 💰 Depósitos
- 📤 Saques
- 🔄 Transferências
- 📊 Relatórios

## 📱 Responsividade

O front-end é totalmente responsivo e se adapta a:
- 📱 Smartphones (< 768px)
- 📱 Tablets (768px - 1024px)
- 💻 Desktops (> 1024px)

## 🔔 Notificações Toast

Sistema de notificações que aparece no canto inferior direito:

- **Sucesso** (verde): Operações bem-sucedidas
- **Erro** (vermelho): Erros de validação ou API
- **Aviso** (laranja): Avisos importantes
- **Info** (azul): Informações gerais

## 🌐 API Endpoints Consumidos

### Contas
```javascript
GET    /api/contas              // Listar contas
POST   /api/contas              // Criar conta
GET    /api/contas/{numero}     // Buscar conta
DELETE /api/contas/{numero}     // Encerrar conta
```

### Operações
```javascript
POST /api/contas/{numero}/deposito      // Depósito
POST /api/contas/{numero}/saque         // Saque
POST /api/contas/transferencia          // Transferência
GET  /api/contas/{numero}/saldo         // Consultar saldo
```

### Histórico
```javascript
GET /api/historico/conta/{numero}                    // Histórico completo
GET /api/historico/conta/{numero}/periodo           // Histórico por período
GET /api/historico/extrato/{numero}                 // Extrato (procedure)
```

### Relatórios
```javascript
GET  /api/historico/relatorio/saldo-total            // Estatísticas
GET  /api/historico/relatorio/contas-saldo-baixo     // Contas baixo saldo
GET  /api/historico/relatorio/movimentacoes          // Movimentações
```

## 🔧 Funções JavaScript Principais

### Utilitárias
```javascript
showToast(message, type)        // Exibir notificação
showSection(sectionId)          // Navegar entre seções
formatarMoeda(valor)            // Formatar R$ 1.000,00
formatarData(dataString)        // Formatar 24/11/2024 10:30
```

### Operações
```javascript
criarConta(event)               // Criar nova conta
realizarDeposito(event)         // Fazer depósito
realizarSaque(event)            // Fazer saque
realizarTransferencia(event)    // Fazer transferência
consultarSaldo(event)           // Consultar saldo
encerrarConta(event)            // Encerrar conta
```

### Visualização
```javascript
carregarContas()                // Carregar lista de contas
consultarHistorico(event)       // Consultar extrato
carregarSaldoTotal()            // Carregar estatísticas
carregarContasSaldoBaixo()      // Contas com saldo baixo
```

## 🐛 Tratamento de Erros

Todos os erros são capturados e exibidos:

1. **Erros de API**: Mensagens do backend
2. **Erros de Rede**: "Erro ao conectar com o servidor"
3. **Validações**: Campos obrigatórios no HTML5

## ✨ Melhorias Futuras (Opcionais)

- [ ] Autenticação e login de usuários
- [ ] Dashboard com gráficos (Chart.js)
- [ ] Exportar relatórios em PDF
- [ ] Dark mode
- [ ] Pesquisa e filtros avançados
- [ ] Paginação para grandes listas
- [ ] WebSocket para atualizações em tempo real

## 🎓 Conceitos Demonstrados

- ✅ **Fetch API**: Requisições HTTP assíncronas
- ✅ **DOM Manipulation**: Atualização dinâmica da interface
- ✅ **Event Handling**: Formulários e cliques
- ✅ **CSS Grid/Flexbox**: Layout responsivo
- ✅ **CSS Animations**: Transições suaves
- ✅ **Error Handling**: Try-catch e feedback visual
- ✅ **REST API Consumption**: CRUD completo

## 📸 Screenshots

### Tela Inicial
```
┌─────────────────────────────────────────┐
│  ByteBank - Sistema Bancário            │
│  ─────────────────────────────          │
│  Bem-vindo ao ByteBank                  │
│  Sistema Bancário Moderno...            │
│                                         │
│  [Contas] [Operações] [Histórico]      │
│  [Relatórios]                          │
└─────────────────────────────────────────┘
```

### Cards de Conta
```
┌───────────────────┐
│ 💳 1001    [ATIVA]│
│ R$ 5.000,00       │
│ Titular: João     │
│ CPF: 123...       │
└───────────────────┘
```

## 🆘 Troubleshooting

### "Failed to fetch"
→ Certifique-se que o backend está rodando na porta 8080

### CORS Error
→ A configuração CORS já foi adicionada ao Spring Boot

### Página em branco
→ Abra o Console do navegador (F12) para ver erros

### Botões não funcionam
→ Verifique se o script.js foi carregado corretamente

## 📚 Recursos Utilizados

- **Font Awesome 6**: Ícones
- **CSS Grid**: Layout responsivo
- **Fetch API**: Requisições AJAX
- **JavaScript ES6+**: Async/await, arrow functions

---

**Interface desenvolvida para o ByteBank - Sistema Bancário Acadêmico** 🎓

