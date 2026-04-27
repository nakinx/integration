package org.acme.resources;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.business.ColaboradorBusiness;
import org.acme.dto.ColaboradorAuth;
import org.acme.dto.LoginRequest;
import org.acme.dto.TokenResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.Optional;

/**
 * Endpoint de autenticação para geração de token JWT.
 * 
 * Valida credenciais contra a tabela colaborador e retorna
 * um token JWT com as roles do colaborador.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação", description = "Endpoints para obtenção de token JWT")
public class AuthResource {

    private static final Logger logger = Logger.getLogger(AuthResource.class);
    private static final String ISSUER = "https://integration.acme.org/issuer";
    private static final long EXPIRATION_SECONDS = 3600L; // 1 hora

    @Inject
    ColaboradorBusiness colaboradorBusiness;

    @POST
    @Path("/login")
    @PermitAll
    @Operation(
        summary = "Autenticar colaborador",
        description = "Gera um token JWT para colaboradores autenticados. " +
                      "Use o email e senha cadastrados na tabela colaborador."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = TokenResponse.class)
            )
        ),
        @APIResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public Response login(@Valid LoginRequest request) {
        Optional<ColaboradorAuth> authResult = colaboradorBusiness.authenticate(
                request.getUsername(), 
                request.getPassword()
        );

        if (authResult.isEmpty()) {
            logger.warn("Tentativa de login falhou para: " + request.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }

        ColaboradorAuth auth = authResult.get();
        
        String token = Jwt.issuer(ISSUER)
                .upn(auth.getEmail())
                .groups(auth.getRoles())
                .expiresIn(Duration.ofSeconds(EXPIRATION_SECONDS))
                .sign();

        logger.info("Login realizado com sucesso para: " + auth.getEmail());

        return Response.ok(new TokenResponse(token, "Bearer", EXPIRATION_SECONDS)).build();
    }
}
