# Integrações

Este projeto tem como objetivo testar as habilidades de integrações utilizando REST API.

## Configuração Inicial do Banco de Dados

Esta aplicação utiliza PostgreSQL como banco de dados e Hibernate ORM para persistência de dados.

Ao criar o banco de dados pela primeira vez, configure o Hibernate para criar as tabelas automaticamente:

1. **Edite `src/main/resources/application.properties`:**
   ```properties
   quarkus.hibernate-orm.database.generation=create
   ```

2. **Inicie a aplicação:**
   ```shell script
   # Com Docker Compose
   docker-compose up --build
   ```

3. **Após as tabelas serem criadas, volte para o modo validação:**
   ```properties
   quarkus.hibernate-orm.database.generation=validate
   ```

## Executando o Projeto

O projeto inclui um `docker-compose.yml` com containers PostgreSQL e Quarkus:

```shell script
# Iniciar todos os serviços
docker-compose up --build

# Iniciar em segundo plano
docker-compose up -d --build

# Parar serviços
docker-compose down

# Ver logs
docker-compose logs -f
```

## Acesso ao container de Banco de Dados

**Conectar ao container PostgreSQL:**
```shell script
docker exec -it integration-postgres psql -U postgres -d integration_db
```

## Operações da API de Clientes

### 1. Listar todos os clientes (GET)

```bash
curl -X GET http://localhost:8080/clientes \
  -H "Content-Type: application/json"
```

### 2. Obter um cliente específico (GET)

```bash
curl -X GET http://localhost:8080/clientes/1 \
  -H "Content-Type: application/json"
```

### 3. Criar um novo cliente (POST) ⭐

Você só precisa fornecer: `nome`, `email` e `cep`. Os campos `logradouro`, `bairro`, `localidade` e `uf` serão **preenchidos automaticamente**:

```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ismael Filipe",
    "email": "ismael@exemplo.com",
    "cep": "71670012"
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nome": "Ismael Filipe",
  "email": "ismael@example.com",
  "cep": "70670012",
  "logradouro": "Quadra 100",
  "bairro": "Setor Sudoeste",
  "localidade": "Brasília",
  "uf": "DF"
}
```

### 4. Atualizar um cliente (PUT)

```bash
curl -X PUT http://localhost:8080/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ismael Filipe",
    "email": "ismael@email.com",
    "cep": "70670012",
    "uf": "DF",
    "localidade": "Brasilia",
    "bairro": "Setor Sudoeste",
    "logradouro": "Quadra 100"
  }'
```

### 5. Deletar um cliente (DELETE)

```bash
curl -X DELETE http://localhost:8080/clientes/1 \
  -H "Content-Type: application/json"
```

