package org.acme.business;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.ColaboradorAuthDTO;
import org.acme.entities.Colaborador;
import org.acme.repositories.ColaboradorRepository;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class ColaboradorBusiness {

    private static final Logger logger = Logger.getLogger(ColaboradorBusiness.class);

    @Inject
    ColaboradorRepository colaboradorRepository;

    /**
     * Valida credenciais contra o banco de dados.     
     */
    public Optional<ColaboradorAuth> authenticate(String email, String password) {

        Optional<Colaborador> colaboradorOpt = colaboradorRepository.findByEmail(email);

        if (colaboradorOpt.isEmpty()) {
            return Optional.empty();
        }

        Colaborador colaborador = colaboradorOpt.get();

        // Verifica senha usando BCrypt
        if (!BcryptUtil.matches(password, colaborador.getSenha())) {
            return Optional.empty();
        }

        // Converte string de roles em Set
        Set<String> roles = new HashSet<>(Arrays.asList(colaborador.getRoles().split(",")));

        return Optional.of(new ColaboradorAuth(colaborador.getEmail(), roles));
    }
}
