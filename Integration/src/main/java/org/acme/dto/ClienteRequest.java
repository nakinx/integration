package org.acme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class ClienteRequest {

    @Schema(
        description = "Nome completo do cliente",
        required = true,
        example = "João Silva"
    )
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Schema(
        description = "E-mail do cliente",
        required = true,
        example = "joao.silva@example.com"
    )
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    private String email;

    @Schema(
        description = "CEP do cliente (8 dígitos)",
        required = true,
        example = "01310100"
    )
    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    public ClienteRequest() {
    }

    public ClienteRequest(String nome, String email, String cep) {
        this.nome = nome;
        this.email = email;
        this.cep = cep;
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
}
