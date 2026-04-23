package org.acme.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entities.Cliente;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {

    public Cliente findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
