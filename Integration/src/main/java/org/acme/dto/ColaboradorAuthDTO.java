package org.acme.dto;

import java.util.Set;

public class ColaboradorAuth {

    private String email;
    private Set<String> roles;

    public ColaboradorAuth() {}

    public ColaboradorAuth(String email, Set<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
