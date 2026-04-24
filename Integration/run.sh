#!/bin/bash

# Script para refazer a build e reiniciar containers

# Definir diretório do script (onde está o docker-compose.yml)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Tratar erros mais graciosamente
set -o pipefail

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens
print_info() {
    echo -e "${BLUE} $1${NC}"
}

print_success() {
    echo -e "${GREEN} $1${NC}"
}

print_warning() {
    echo -e "${YELLOW} $1${NC}"
}

print_error() {
    echo -e "${RED} $1${NC}"
}

# Função para mostrar uso
show_usage() {
    echo "Uso: ./rebuild.sh [opção]"
    echo ""
    echo "Opções:"
    echo "  (Sem opcao)  Rebuild completo e restart dos containers"
    echo "  --build-only Apenas rebuild do projeto Maven"
    echo "  --restart    Apenas restart dos containers"
    echo "  --stop       Para os containers"
    echo "  --logs       Mostra logs dos containers"
    echo "  --db         Conecta ao banco de dados PostgreSQL"
    echo "  --test       Executa os testes unitários e mostra resultados"
    echo "  --help       Mostra esta mensagem"
}

# Função para fazer rebuild
rebuild_project() {
    print_info "Iniciando rebuild do projeto Maven..."
    
    print_info "Limpando build anterior..."
    ./mvnw clean
    
    print_info "Compilando projeto..."
    ./mvnw compile
    
    print_info "Empacotando aplicação..."
    ./mvnw package -DskipTests
    
    print_success "Projeto reconstruído com sucesso!"
}

# Função para reiniciar containers
restart_containers() {
    print_info "Parando containers..."
    docker-compose -f "$SCRIPT_DIR/docker-compose.yml" down
    
    print_info "Reconstruindo e iniciando containers..."
    docker-compose -f "$SCRIPT_DIR/docker-compose.yml" up --build -d
    
    print_success "Containers reiniciados com sucesso!"
    
    print_info "Aguardando aplicação iniciar..."
    sleep 5
    
    print_info "Verificando status dos containers..."
    docker-compose -f "$SCRIPT_DIR/docker-compose.yml" ps
}

# Função para parar containers
stop_containers() {
    print_info "Parando containers..."
    docker-compose -f "$SCRIPT_DIR/docker-compose.yml" down
    print_success "Containers parados!"
}

# Função para mostrar logs
show_logs() {
    print_info "Mostrando logs dos containers..."
    docker-compose -f "$SCRIPT_DIR/docker-compose.yml" logs -f
}

# Função para conectar ao banco de dados
db_shell() {
    print_info "Conectando ao banco de dados PostgreSQL..."
    docker-compose -f "$SCRIPT_DIR/docker-compose.yml" exec postgres psql -U postgres -d integration_db
}

# Função para executar testes unitários
run_tests() {
    print_info "## EXECUTANDO TESTES UNITÁRIOS ## "
    echo ""
    
    # Executar testes e capturar saída
    TEST_OUTPUT=$(./mvnw test 2>&1)
    TEST_EXIT_CODE=$?
    
    # Extrair informações relevantes
    TESTS_RUN=$(echo "$TEST_OUTPUT" | grep -o "Tests run: [0-9]*" | tail -1 | grep -o "[0-9]*")
    FAILURES=$(echo "$TEST_OUTPUT" | grep -o "Failures: [0-9]*" | tail -1 | grep -o "[0-9]*")
    ERRORS=$(echo "$TEST_OUTPUT" | grep -o "Errors: [0-9]*" | tail -1 | grep -o "[0-9]*")
    SKIPPED=$(echo "$TEST_OUTPUT" | grep -o "Skipped: [0-9]*" | tail -1 | grep -o "[0-9]*")
    
    # Mostrar resumo por classe de teste
    print_info "Resultados por classe de teste:"
    echo ""
    echo "$TEST_OUTPUT" | grep -E "Tests run: [0-9]+.*in org\.acme\." | while read -r line; do
        CLASS_NAME=$(echo "$line" | grep -o "in org\.acme\.[^ ]*" | sed 's/in //')
        RESULT=$(echo "$line" | grep -o "Tests run: [0-9]*, Failures: [0-9]*, Errors: [0-9]*, Skipped: [0-9]*")
        
        # Colorir baseado no resultado
        if echo "$line" | grep -q "Failures: 0.*Errors: 0"; then
            echo -e "  ${GREEN}${NC} $CLASS_NAME"
            echo -e "    ${BLUE}$RESULT${NC}"
        else
            echo -e "  ${RED}${NC} $CLASS_NAME"
            echo -e "    ${RED}$RESULT${NC}"
        fi
        echo ""
    done
    
    # Mostrar resumo geral
    echo ""
    print_info "## RESUMO GERAL DOS TESTES ##"
    echo ""
    
    if [ ! -z "$TESTS_RUN" ]; then
        echo -e "  Total de testes:${NC}     $TESTS_RUN"
        
        if [ "$FAILURES" = "0" ]; then
            echo -e "  Falhas:${NC}              $FAILURES"
        else
            echo -e "  Falhas:${NC}              $FAILURES"
        fi
        
        if [ "$ERRORS" = "0" ]; then
            echo -e "  Erros:${NC}               $ERRORS"
        else
            echo -e "  Erros:${NC}               $ERRORS"
        fi
        
        echo -e "  Ignorados:${NC}           $SKIPPED"
        echo ""
        
        # Calcular taxa de sucesso
        if [ "$TESTS_RUN" -gt 0 ]; then
            PASSED=$((TESTS_RUN - FAILURES - ERRORS - SKIPPED))
            SUCCESS_RATE=$((PASSED * 100 / TESTS_RUN))
            echo -e "  Taxa de sucesso:${NC}     ${SUCCESS_RATE}%"
        fi
    fi
    
    echo ""
    print_info "═════════════════════════════════════════════"
    
    # Verificar resultado e mostrar mensagem apropriada
    if [ $TEST_EXIT_CODE -eq 0 ]; then
        print_success "TODOS OS TESTES PASSARAM COM SUCESSO!"
        echo ""
        return 0
    else
        print_error "ALGUNS TESTES FALHARAM!"
        echo ""
        print_warning "Execute './mvnw test' para ver detalhes completos dos erros."
        echo ""
        return 1
    fi
}

# Main
case "${1:-}" in
    --build-only)
        rebuild_project
        ;;
    --restart)
        restart_containers
        ;;
    --stop)
        stop_containers
        ;;
    --logs)
        show_logs
        ;;
    --db)
        db_shell
        ;;
    --test)
        run_tests
        ;;
    --help)
        show_usage
        ;;
    "")
        print_info "Iniciando rebuild completo..."
        print_info "═════════════════════════════════════════════"
        
        rebuild_project
        
        print_info "═════════════════════════════════════════════"
        restart_containers
        
        print_info "═════════════════════════════════════════════"
        print_success "Build e containers reiniciados com sucesso!"
        ;;
    *)
        print_error "Opção desconhecida: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac
