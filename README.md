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