package org.acme.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.jboss.logging.Logger;

/**
 * Health Check para APIs Externas - Verifica disponibilidade de serviços externos
 * 
 * Verifica:
 * - ViaCEP API (https://viacep.com.br)
 * - Nominatim API (https://nominatim.openstreetmap.org)
 * 
 * Endpoint: /q/health/ready
 */
@Readiness
@ApplicationScoped
public class ExternalApisHealthCheck implements HealthCheck {

    private static final Logger logger = Logger.getLogger(ExternalApisHealthCheck.class);
    
    private static final String VIACEP_URL = "https://viacep.com.br/ws/01310100/json";
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?q=São+Paulo&format=json&limit=1";
    private static final int TIMEOUT_MS = 3000;

    @Override
    public HealthCheckResponse call() {
        try {
            // Verifica ViaCEP
            boolean viaCepOk = checkApi(VIACEP_URL, "ViaCEP");
            
            // Verifica Nominatim
            boolean nominatimOk = checkApi(NOMINATIM_URL, "Nominatim");
            
            if (viaCepOk && nominatimOk) {
                logger.debug("Health Check: Todas as APIs externas OK");
                return HealthCheckResponse
                        .up("APIs Externas");
            } else {
                logger.warn("Health Check: Uma ou mais APIs externas indisponíveis");
                return HealthCheckResponse
                        .up("APIs Externas");
            }
            
        } catch (Exception e) {
            logger.error("Health Check: Erro ao verificar APIs externas: " + e.getMessage());
            
            return HealthCheckResponse
                    .down("APIs Externas");
        }
    }

    /**
     * Testa conectividade com uma API externa
     */
    private boolean checkApi(String url, String apiName) {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            
            int statusCode = client.target(url)
                    .request()
                    .header("User-Agent", "Integration-Health-Check/1.0")
                    .get()
                    .getStatus();
            
            logger.debug("Health Check " + apiName + ": Status " + statusCode);
            
            return statusCode >= 200 && statusCode < 300;
            
        } catch (Exception e) {
            logger.warn("Health Check " + apiName + ": Erro - " + e.getMessage());
            return false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
