package org.acme.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    @Column(name = "cep", length = 9, nullable = false)
    private String cep;

    @Column(name = "uf", length = 2, nullable = false)
    private String uf;

    @Column(name = "localidade", length = 100, nullable = false)
    private String localidade;

    @Column(name = "bairro", length = 100, nullable = false)
    private String bairro;

    @Column(name = "logradouro", length = 100, nullable = false)
    private String logradouro;

    // Construtores
    public Cliente() {
    }

    public Cliente(String nome, String email, String cep, String uf, String localidade, String bairro, String logradouro) {
        this.nome = nome;
        this.email = email;
        this.cep = cep;
        this.uf = uf;
        this.localidade = localidade;
        this.bairro = bairro;
        this.logradouro = logradouro;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cep='" + cep + '\'' +
                ", uf='" + uf + '\'' +
                ", localidade='" + localidade + '\'' +
                ", bairro='" + bairro + '\'' +
                ", logradouro='" + logradouro + '\'' +
                '}';
    }
}
