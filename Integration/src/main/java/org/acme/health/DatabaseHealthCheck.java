package org.acme.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.logging.Logger;

/**
 * Health Check de Readiness - Verifica se a aplicação está pronta para receber requisições
 * 
 * Verifica:
 * - Conexão com PostgreSQL
 * - Disponibilidade do banco de dados
 * 
 * Endpoint: /q/health/ready
 */
@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    private static final Logger logger = Logger.getLogger(DatabaseHealthCheck.class);
    
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public HealthCheckResponse call() {
        try {
            // Testa conexão com banco de dados
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            
            logger.debug("Health Check: Banco de dados OK");
            
            return HealthCheckResponse
                    .up("Banco de Dados");
                    
        } catch (Exception e) {
            logger.warn("Health Check: Erro ao conectar banco de dados: " + e.getMessage());
            
            return HealthCheckResponse
                    .down("Banco de Dados");
        }
    }
}
