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
    echo -e "${BLUE}  $1${NC}"
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
        print_success "Build e containers reiniciados com sucesso! 🚀"
        print_info "Aplicação disponível em: http://localhost:8080"
        ;;
    *)
        print_error "Opção desconhecida: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac
