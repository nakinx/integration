
# Perfil: Integrações

## Objetivo
Desenvolver uma aplicação backend que consuma dados de uma ou mais APIs externas, aplique regras de transformação e normalização e exponha um contrato interno consistente.

## O foco do desafio é avaliar:
* Capacidade de desenho técnico
* Integração entre sistemas
* Resiliência
* Qualidade de código
* Observabilidade
* Clareza nas decisões técnicas
* A linguagem, framework e bibliotecas são livres.


## Cenário Proposto
Considere que o sistema precisa integrar provedores externos que retornam estruturas, nomenclaturas, status e formatos diferentes.

### Sua aplicação deverá:

* Consumir pelo menos 2 fontes externas distintas
* Tratar diferenças entre contratos
* Normalizar dados recebidos
* Expor uma API interna padronizada

### As APIs externas podem ser:
* APIs públicas reais
* Mocks criados por você
* Serviços simulados localmente


### Requisitos mínimos

### Funcionais
* Integração com provedores externos
* Autenticação quando aplicável
* Tradução de payloads para modelo canônico interno
* Normalização de status e valores divergentes
* Persistência dos dados processados
* Endpoints para consulta e filtros
* Reprocessamento ou sincronização sob demanda

### Técnicos esperados (nível sênior)
* Arquitetura organizada por responsabilidades
* retry / timeout / circuit breaker / fallback
* Idempotência
* Logs estruturados
* Métricas / health checks
* Documentação de trade-offs técnicos

### Segurança
* Autenticação/autorização da API
* Gestão segura de secrets
* Evitar exposição sensível em logs


## Infraestrutura
* Dockerfile
* docker-compose.yml (ou equivalente)
* Instruções claras de execução
* Testes
* Testes unitários
* Testes de integração
* Cenários de falha e timeout


## Entregáveis
* Código-fonte versionado
* Link do repositório
* Dockerfile + compose
* README.md
* Documentação da API
* Exemplos/mocks utilizados