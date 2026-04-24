# Testes Unitários e de Integração

## Resumo
Este projeto contém **33 testes** que cobrem a camada de negócio, endpoints REST e validações de DTO.

## Estrutura de Testes

### 📦 Testes de Negócio (12 testes)
**Arquivo:** `org.acme.business.ClienteBusinessTest`

Testes unitários da camada de negócio usando **Mockito** para mockar dependências:

- ✅ `testListar_DeveRetornarListaDeClientes` - Verifica listagem de clientes
- ✅ `testObter_DeveRetornarClientePorId` - Verifica busca por ID
- ✅ `testCriar_ComSucesso_SemCEP` - Criação sem CEP
- ✅ `testCriar_ComSucesso_ComCEPValido` - Criação com busca de endereço e coordenadas
- ✅ `testCriar_DeveLancarExcecao_EmailJaCadastrado` - Validação de email único
- ✅ `testCriar_DeveLancarExcecao_CepInvalido` - Validação de CEP inválido
- ✅ `testAtualizar_ComSucesso_SemAlterarCEP` - Atualização mantendo CEP
- ✅ `testAtualizar_ComSucesso_AlterandoCEP` - Atualização com novo CEP
- ✅ `testAtualizar_DeveLancarExcecao_ClienteNaoEncontrado` - Cliente não existe
- ✅ `testAtualizar_DeveLancarExcecao_EmailJaCadastrado` - Email duplicado na atualização
- ✅ `testDeletar_ComSucesso` - Deleção bem-sucedida
- ✅ `testDeletar_DeveLancarExcecao_ClienteNaoEncontrado` - Cliente não existe para deletar

### 🌐 Testes de Integração REST (12 testes)
**Arquivo:** `org.acme.resources.ClienteResourceTest`

Testes de integração dos endpoints REST usando **RestAssured** e **@QuarkusTest**:

- ✅ `testListar_DeveRetornarStatus200ComListaDeClientes` - GET /clientes
- ✅ `testObter_DeveRetornarStatus200ComCliente` - GET /clientes/{id}
- ✅ `testObter_DeveRetornarStatus404QuandoClienteNaoExiste` - GET /clientes/{id} (404)
- ✅ `testCriar_DeveRetornarStatus201ComClienteCriado` - POST /clientes (201)
- ✅ `testCriar_DeveRetornarStatus409QuandoEmailJaCadastrado` - POST /clientes (409)
- ✅ `testCriar_DeveRetornarStatus400QuandoCepInvalido` - POST /clientes (400)
- ✅ `testCriar_DeveRetornarStatus400QuandoDadosInvalidos` - POST /clientes validação
- ✅ `testAtualizar_DeveRetornarStatus200ComClienteAtualizado` - PUT /clientes/{id}
- ✅ `testAtualizar_DeveRetornarStatus404QuandoClienteNaoExiste` - PUT /clientes/{id} (404)
- ✅ `testAtualizar_DeveRetornarStatus409QuandoEmailJaCadastrado` - PUT /clientes/{id} (409)
- ✅ `testDeletar_DeveRetornarStatus204QuandoClienteDeletado` - DELETE /clientes/{id}
- ✅ `testDeletar_DeveRetornarStatus404QuandoClienteNaoExiste` - DELETE /clientes/{id} (404)

### ✅ Testes de Validação (9 testes)
**Arquivo:** `org.acme.dto.ClienteRequestTest`

Testes de validação do DTO usando **Bean Validation**:

- ✅ `testClienteRequestValido` - Dados válidos
- ✅ `testClienteRequest_NomeVazio_DeveRetornarErro` - Nome vazio
- ✅ `testClienteRequest_NomeNulo_DeveRetornarErro` - Nome nulo
- ✅ `testClienteRequest_EmailVazio_DeveRetornarErro` - Email vazio
- ✅ `testClienteRequest_EmailInvalido_DeveRetornarErro` - Email inválido
- ✅ `testClienteRequest_CepVazio_DeveRetornarErro` - CEP vazio
- ✅ `testClienteRequest_TodosCamposInvalidos_DeveRetornarMultiplosErros` - Múltiplos erros
- ✅ `testClienteRequest_Construtor` - Teste do construtor
- ✅ `testClienteRequest_GettersSetters` - Teste de getters/setters

## Tecnologias Utilizadas

- **JUnit 5** - Framework de testes
- **Mockito** - Framework de mocking
- **RestAssured** - Testes de API REST
- **Quarkus Test** - Contexto de teste do Quarkus
- **H2 Database** - Banco de dados em memória para testes
- **Bean Validation** - Validação de dados

## Executar os Testes

```bash
# Executar todos os testes
./mvnw test

# Executar apenas uma classe de teste
./mvnw test -Dtest=ClienteBusinessTest

# Executar testes com relatório detalhado
./mvnw test -X
```

## Cobertura de Testes

Os testes cobrem:
- ✅ Camada de negócio (ClienteBusiness)
- ✅ Endpoints REST (ClienteResource)
- ✅ Validações de DTO (ClienteRequest)
- ✅ Tratamento de exceções customizadas
- ✅ Integração com serviços externos (mockados)
- ✅ Códigos de status HTTP corretos
- ✅ Validações de Bean Validation

## Configuração de Testes

Os testes utilizam H2 in-memory database configurado em:
- `src/test/resources/application.properties`

## Resultado

```
Tests run: 33, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
