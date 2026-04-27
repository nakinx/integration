-- Script de inicialização do banco de dados PostgreSQL

CREATE TABLE IF NOT EXISTS colaborador (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL DEFAULT 'user'
);

-- Inserir os usuarios iniciais para consumo das apis de cliente
INSERT INTO colaborador (nome, email, senha, roles) 
VALUES 
('Administrador', 'admin@integracao.com', '$2a$10$XeVXs0RtQgTZEMjEyn4mHuuUZkvXF8oN3vt1MVVz7MP8Ysj4LER9u', 'admin,user'),
('Usuario', 'user@integracao.com', '$2a$10$Mrf/vcMPxkZrZ6PVK9MPy.sGY7iNVrCynDfu4nIcClvqV88.XTLd6', 'user')
ON CONFLICT (email) DO NOTHING;

CREATE TABLE IF NOT EXISTS cliente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(255),
    bairro VARCHAR(255),
    localidade VARCHAR(255),
    uf VARCHAR(2),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE INDEX IF NOT EXISTS idx_cliente_email ON cliente(email);

CREATE INDEX IF NOT EXISTS idx_colaborador_email ON colaborador(email);
