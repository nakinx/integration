-- Script de inicialização de dados para testes
-- Senhas (em BCrypt):
-- admin123 -> $2a$10$XeVXs0RtQgTZEMjEyn4mHuuUZkvXF8oN3vt1MVVz7MP8Ysj4LER9u
-- user123  -> $2a$10$Mrf/vcMPxkZrZ6PVK9MPy.sGY7iNVrCynDfu4nIcClvqV88.XTLd6

INSERT INTO colaborador (id, nome, email, senha, roles) VALUES 
(1, 'Admin Teste', 'admin@acme.org', '$2a$10$XeVXs0RtQgTZEMjEyn4mHuuUZkvXF8oN3vt1MVVz7MP8Ysj4LER9u', 'admin,user'),
(2, 'User Teste', 'user@acme.org', '$2a$10$Mrf/vcMPxkZrZ6PVK9MPy.sGY7iNVrCynDfu4nIcClvqV88.XTLd6', 'user');
