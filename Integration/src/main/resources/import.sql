-- Script de inicialização de dados
-- Este arquivo é executado automaticamente pelo Hibernate no startup em modo dev/test

-- Colaboradores de exemplo
-- Senhas (em BCrypt):
-- admin123 -> $2a$10$XeVXs0RtQgTZEMjEyn4mHuuUZkvXF8oN3vt1MVVz7MP8Ysj4LER9u
-- user123  -> $2a$10$Mrf/vcMPxkZrZ6PVK9MPy.sGY7iNVrCynDfu4nIcClvqV88.XTLd6

INSERT INTO colaborador (nome, email, senha, roles) VALUES 
('Administrador', 'admin@integracao.com', '$2a$10$XeVXs0RtQgTZEMjEyn4mHuuUZkvXF8oN3vt1MVVz7MP8Ysj4LER9u', 'admin,user'),
('Usuario', 'usuario@integracao.com', '$2a$10$Mrf/vcMPxkZrZ6PVK9MPy.sGY7iNVrCynDfu4nIcClvqV88.XTLd6', 'user');
