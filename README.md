# Integração

Este projeto tem como objetivo avaliar habilidades de integração utilizando APIs REST.

Como parte do exercício, a aplicação permite o cadastro de clientes e, a partir da informação de CEP, realiza a consulta de dados de endereço por meio de uma API externa. Com o endereço obtido, o sistema também consome outra API para recuperar as coordenadas geográficas (latitude e longitude) correspondentes ao local.

Além disso, o projeto expõe APIs para consumo externo que permitem listar, criar, alterar e excluir clientes, seguindo os princípios de uma arquitetura RESTful.

## Arquitetura

O projeto foi desenvolvido em Java, utilizando o framework Quarkus, com foco em desempenho e produtividade no desenvolvimento de aplicações backend.

Para persistência dos dados, é utilizado um banco de dados relacional PostgreSQL, garantindo confiabilidade e consistência das informações armazenadas.

### Fluxo dos Dados

HTTP Request
    ↓
[ClienteResource] ← Recebe requisição
    ↓
[ClienteBusiness] ← Processa lógica
    ↓
[ClienteRepository] ← Persiste dados
    ↓
[Cliente] ← Entidade mapeada
    ↓
[Banco de Dados]

### Estrutura de Pacotes

```
org/acme/
├── business/ .................................. Lógica de negócio                                   
│   └── ClienteBusiness.java 
├── resources/ ................................. Endpoints HTTP REST
│   └── ClienteResource.java 
├── services/ .................................. Integração com APIs externas
│   ├── ViaCEPService.java 
│   └── NominatimService.java 
├── repositories/ .............................. Acesso a dados no banco
│   └── ClienteRepository.java 
├── entities/ .................................. Entidade do banco de dados
│   └── Cliente.java  
├── dto/ ....................................... Transferência de dados entre camadas
│   ├── ClienteRequest.java 
│   ├── ViaCEPResponse.java .................... ViaCEP
│   └── NominatimResponse.java ................. Nominatim
└── config/ .................................... Configurações da aplicação
    └── OpenAPIConfig.java
```     

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

O script run.sh auxilia nas principais funcionalidades do ambiente de desenvolvimento da aplicação, automatizando tarefas comuns do dia a dia do desenvolvedor. Abaixo está a lista de funcionalidades disponibilizadas:

  Executa um rebuild completo do projeto e reinicia os containers Docker:

  `./run.sh`

  Executa apenas o rebuild do projeto Maven sem afetar os containers:

  `./run.sh --build-only`

  Reinicia apenas os containers Docker sem rebuild:

  `./run.sh --restart`

  Para todos os containers da aplicação de forma limpa:

  `./run.sh --stop`

  Mostra os logs em tempo real de todos os containers:

  `./run.sh --logs`

  Conecta diretamente ao PostgreSQL via terminal interativo:

  `./run.sh --db`

  Executa todos os testes unitários e de integração com apresentação formatada dos resultados:

  `./run.sh --test`