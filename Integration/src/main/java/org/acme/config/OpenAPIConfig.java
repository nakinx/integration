package org.acme.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "API de Integrações",
        version = "1.0.0",
        description = "Projeto objetivo testar as habilidades de integrações utilizando REST API.",
        contact = @Contact(
            name = "Suporte",
            url = "https://suporte.integracao.com"
        )
    ),
    tags = {
        @Tag(name = "Clientes", description = "Operações relacionadas ao gerenciamento de clientes"),
        @Tag(name = "Health Check", description = "Endpoints de verificação de saúde da aplicação")
    }
)
@SecurityScheme(
    securitySchemeName = "Bearer",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Autenticação com JWT Token"
)
public class OpenAPIConfig {
}
