package org.acme.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Health Check de Liveness - Verifica se a aplicação está viva/funcionando
 * 
 * Endpoint: /q/health/live
 */
@Liveness
@ApplicationScoped
public class ApplicationHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        try {
            return HealthCheckResponse
                    .up("Aplicação");
        } catch (Exception e) {
            return HealthCheckResponse
                    .down("Aplicação");
        }
    }
}
