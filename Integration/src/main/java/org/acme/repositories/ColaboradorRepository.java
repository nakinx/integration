package org.acme.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entities.Colaborador;

import java.util.Optional;

@ApplicationScoped
public class ColaboradorRepository implements PanacheRepository<Colaborador> {

    public Optional<Colaborador> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
