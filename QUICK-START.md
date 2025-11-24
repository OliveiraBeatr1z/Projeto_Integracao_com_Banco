# 🚀 Guia Rápido de Início - ByteBank Spring Boot

## Passos para rodar o projeto

### 1️⃣ Preparar o Banco de Dados

```bash
# Entre no MySQL
mysql -u root -p

# Crie o banco de dados
CREATE DATABASE bytebank;

# Verifique se foi criado
SHOW DATABASES;

# Saia do MySQL
exit;
```

### 2️⃣ Configurar as Credenciais

Edite o arquivo `src/main/resources/application.properties` com suas credenciais do MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bytebank
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

### 3️⃣ Instalar Procedures e Triggers (NOVO!)

```bash
# Execute o script SQL que cria procedures e triggers
mysql -u root -p bytebank < src/main/resources/schema.sql

# Ou dentro do MySQL:
mysql -u root -p
USE bytebank;
SOURCE /caminho/completo/para/src/main/resources/schema.sql;
exit;
```

**O que isso faz:**
- ✅ Cria tabela `historico_transacao` para auditoria
- ✅ Cria 2 triggers que registram automaticamente todas as operações
- ✅ Cria 5 procedures para relatórios e operações avançadas

### 4️⃣ Executar a Aplicação

```bash
# Via Maven
mvn spring-boot:run

# OU compilar e executar o JAR
mvn clean package
java -jar target/bank-1.0-SNAPSHOT.jar
```

### 5️⃣ Testar a API

Abra um novo terminal e teste:

```bash
# Criar primeira conta
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{
    "numero": 1001,
    "dadosCliente": {
      "nome": "Maria Silva",
      "cpf": "12345678901",
      "email": "maria@email.com"
    }
  }'

# Fazer depósito
curl -X POST http://localhost:8080/api/contas/1001/deposito \
  -H "Content-Type: application/json" \
  -d '{"valor": 1000.00}'

# Consultar saldo
curl http://localhost:8080/api/contas/1001/saldo

# Listar todas as contas
curl http://localhost:8080/api/contas
```

### 6️⃣ Testar Procedures e Triggers (NOVO!)

```bash
# Criar conta (Trigger registra automaticamente!)
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{
    "numero": 3001,
    "dadosCliente": {
      "nome": "João Silva",
      "cpf": "12345678901",
      "email": "joao@email.com"
    }
  }'

# Fazer depósito (Trigger registra!)
curl -X POST http://localhost:8080/api/contas/3001/deposito \
  -H "Content-Type: application/json" \
  -d '{"valor": 5000.00}'

# Ver histórico de transações (criado pelos triggers)
curl http://localhost:8080/api/historico/conta/3001

# Consultar saldo total do banco (usando procedure)
curl http://localhost:8080/api/historico/relatorio/saldo-total

# Ver extrato via procedure
curl http://localhost:8080/api/historico/extrato/3001
```

**📚 Mais sobre procedures e triggers:** Veja `PROCEDURES-TRIGGERS.md`

---

- [ ] MySQL está rodando?
- [ ] Banco de dados `bytebank` foi criado?
- [ ] Credenciais no `application.properties` estão corretas?
- [ ] Java 17 ou superior instalado?
- [ ] Maven instalado?
- [ ] Procedures e triggers foram instalados?
- [ ] Porta 8080 está livre?

## 🎯 O que você tem agora

✅ API REST completa com Spring Boot  
✅ JPA/Hibernate para ORM  
✅ 2 Triggers automáticos para auditoria  
✅ 5 Stored Procedures para relatórios  
✅ Histórico completo de todas as transações  

---

1. Use o arquivo `api-examples.http` para testar todos os endpoints
2. Verifique as tabelas criadas no MySQL:
   ```sql
   USE bytebank;
   SHOW TABLES;
   DESCRIBE conta;
   DESCRIBE cliente;
   ```
3. Explore os logs da aplicação para entender o que está acontecendo
4. Leia o `README.md` completo para mais detalhes

## 🆘 Problemas Comuns

### "Access denied for user"
→ Verifique usuário e senha no application.properties

### "Communications link failure"
→ Certifique-se que o MySQL está rodando

### "Port 8080 is already in use"
→ Altere a porta no application.properties: `server.port=8081`

### Tabelas não são criadas
→ Verifique se `spring.jpa.hibernate.ddl-auto=update` está configurado

## 📖 Mais Informações

Consulte o arquivo `README.md` para documentação completa.

