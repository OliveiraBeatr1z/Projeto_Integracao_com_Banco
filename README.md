# 🏦 ByteBank - Sistema Bancário com Spring Boot e JPA/Hibernate

Este projeto foi migrado de um sistema bancário baseado em JDBC para um sistema moderno utilizando **Spring Boot**, **JPA/Hibernate** e **REST API**.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (Hibernate)
- **MySQL 8**
- **Lombok**
- **Maven**
- **HikariCP** (Connection Pool - incluído no Spring Boot)

## 📋 Pré-requisitos

- Java 17 ou superior
- MySQL 8.0 ou superior
- Maven 3.6 ou superior

## 🛠️ Configuração do Banco de Dados

1. Certifique-se de que o MySQL está rodando
2. Crie o banco de dados:

```sql
CREATE DATABASE bytebank;
```

3. Configure as credenciais no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bytebank
spring.datasource.username=root
spring.datasource.password=8250
```

**Observação:** O Hibernate irá criar as tabelas automaticamente com `spring.jpa.hibernate.ddl-auto=update`

## ▶️ Como Executar

### Via Maven:

```bash
mvn spring-boot:run
```

### Via JAR compilado:

```bash
mvn clean package
java -jar target/bank-1.0-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

## 🎨 Interface Web (Front-End)

Este projeto inclui uma **interface web moderna e responsiva**!

### Acesso Rápido
Após iniciar a aplicação, acesse:
```
http://localhost:8080
```

### Funcionalidades da Interface
- ✅ **Dashboard Interativo**: Visão geral do banco
- ✅ **Gerenciar Contas**: Criar, listar e visualizar contas
- ✅ **Operações**: Depósito, saque e transferência
- ✅ **Histórico**: Extrato completo com filtros
- ✅ **Relatórios**: Estatísticas e análises visuais
- ✅ **Design Responsivo**: Funciona em mobile e desktop
- ✅ **Notificações Toast**: Feedback visual instantâneo

**📚 Documentação completa:** Veja `FRONTEND.md`

---

Este projeto inclui **Stored Procedures** e **Triggers** para demonstrar conceitos avançados de SQL:

### ⚡ Triggers Automáticos
- **Registro de transações**: Toda operação (depósito, saque, transferência) é registrada automaticamente
- **Auditoria completa**: Histórico de todas as operações com data/hora
- **Zero configuração**: Os triggers funcionam automaticamente

### 🔧 Stored Procedures
- `sp_extrato_conta` - Extrato detalhado por período
- `sp_saldo_total_banco` - Estatísticas gerais do banco
- `sp_contas_saldo_baixo` - Contas com saldo abaixo de um limite
- `sp_aplicar_taxa_manutencao` - Aplicar taxa em todas as contas
- `sp_relatorio_movimentacoes` - Relatório de movimentações por tipo

**📚 Documentação completa:** Veja `PROCEDURES-TRIGGERS.md`

### Como usar:
```bash
# 1. Execute o script SQL
mysql -u root -p bytebank < src/main/resources/schema.sql

# 2. Reinicie a aplicação
mvn spring-boot:run

# 3. Teste as procedures via API
curl http://localhost:8080/api/historico/relatorio/saldo-total
```

---

## 📡 Endpoints da API REST

### 📋 Listar Contas Abertas
```http
GET http://localhost:8080/api/contas
```

### ➕ Abrir Nova Conta
```http
POST http://localhost:8080/api/contas
Content-Type: application/json

{
  "numero": 12345,
  "dadosCliente": {
    "nome": "Maria Silva",
    "cpf": "12345678901",
    "email": "maria@email.com"
  }
}
```

### 🔍 Buscar Conta por Número
```http
GET http://localhost:8080/api/contas/12345
```

### 💰 Consultar Saldo
```http
GET http://localhost:8080/api/contas/12345/saldo
```

### 💸 Realizar Saque
```http
POST http://localhost:8080/api/contas/12345/saque
Content-Type: application/json

{
  "valor": 100.00
}
```

### 💵 Realizar Depósito
```http
POST http://localhost:8080/api/contas/12345/deposito
Content-Type: application/json

{
  "valor": 200.00
}
```

### 🔄 Realizar Transferência
```http
POST http://localhost:8080/api/contas/transferencia
Content-Type: application/json

{
  "numeroOrigem": 12345,
  "numeroDestino": 67890,
  "valor": 50.00
}
```

### ❌ Encerrar Conta
```http
DELETE http://localhost:8080/api/contas/12345
```

## 🏗️ Estrutura do Projeto

```
src/main/java/br/com/bank/
├── BytebankApplication.java          # Classe principal do Spring Boot
├── controller/
│   ├── ContaController.java          # REST Controller
│   └── GlobalExceptionHandler.java   # Tratamento de exceções
├── domain/
│   ├── RegraDeNegocioException.java  # Exception customizada
│   ├── cliente/
│   │   ├── Cliente.java              # Entidade JPA Cliente
│   │   └── DadosCadastroCliente.java # DTO
│   └── conta/
│       ├── Conta.java                # Entidade JPA Conta
│       ├── ContaRepository.java      # Repository JPA
│       ├── ContaService.java         # Lógica de negócio
│       └── DadosAberturaConta.java   # DTO
└── resources/
    └── application.properties        # Configurações
```

## 🔄 Principais Mudanças da Migração

### Antes (JDBC Puro):
- ❌ Gestão manual de conexões com ConnectionFactory
- ❌ SQL queries escritas manualmente no DAO
- ❌ Try-catch para cada operação de BD
- ❌ Fechamento manual de connections, statements e resultsets
- ❌ Interface de linha de comando (Scanner)

### Depois (Spring Boot + JPA):
- ✅ Spring gerencia automaticamente as conexões (HikariCP)
- ✅ Hibernate gera SQL automaticamente
- ✅ Transações gerenciadas pelo Spring (@Transactional)
- ✅ Repositories do Spring Data JPA
- ✅ REST API para comunicação
- ✅ Anotações JPA nas entidades
- ✅ Tratamento global de exceções
- ✅ Lombok para reduzir boilerplate

## 🎯 Benefícios da Nova Arquitetura

1. **Produtividade**: Menos código boilerplate
2. **Manutenibilidade**: Código mais limpo e organizado
3. **Escalabilidade**: Preparado para microsserviços
4. **Testabilidade**: Mais fácil criar testes unitários
5. **Integração**: API REST pode ser consumida por qualquer cliente
6. **Segurança**: Pool de conexões gerenciado automaticamente
7. **Performance**: Otimizações automáticas do Hibernate

## 🧪 Testando a API

Você pode usar ferramentas como:
- **Postman**
- **Insomnia**
- **cURL**
- **VS Code REST Client**

### Exemplo com cURL:

```bash
# Criar conta
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{
    "numero": 12345,
    "dadosCliente": {
      "nome": "João Silva",
      "cpf": "12345678901",
      "email": "joao@email.com"
    }
  }'

# Consultar saldo
curl http://localhost:8080/api/contas/12345/saldo

# Fazer depósito
curl -X POST http://localhost:8080/api/contas/12345/deposito \
  -H "Content-Type: application/json" \
  -d '{"valor": 1000.00}'
```

## 📝 Notas Importantes

1. O banco de dados deve estar rodando antes de iniciar a aplicação
2. As tabelas são criadas automaticamente pelo Hibernate
3. Os logs SQL são exibidos no console para debug
4. O pool de conexões HikariCP está configurado para até 10 conexões simultâneas
5. Todas as operações financeiras usam `@Transactional` para garantir consistência

## 🐛 Troubleshooting

### Erro de conexão com MySQL:
```
Verifique se o MySQL está rodando:
sudo systemctl status mysql (Linux)
ou
brew services list (macOS)
```

### Erro de autenticação:
```
Verifique usuário e senha no application.properties
```

### Porta 8080 já em uso:
```
Altere a porta no application.properties:
server.port=8081
```

## 👨‍💻 Desenvolvimento

Para adicionar novas funcionalidades:

1. Crie novas entidades com anotações JPA
2. Crie repositories estendendo `JpaRepository`
3. Implemente a lógica de negócio nos Services
4. Crie endpoints REST nos Controllers
5. Adicione tratamento de exceções se necessário

## 📚 Recursos Adicionais

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

---

**Desenvolvido durante o curso de Lab BD - Projeto de Integração com Banco de Dados**

