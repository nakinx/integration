package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.acme.dto.ViaCEPResponse;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ViaCEPService {

    private static final Logger logger = Logger.getLogger(ViaCEPService.class);
    private static final String VIACEP_BASE_URL = "https://viacep.com.br/ws";
    private static final int TIMEOUT_MS = 5000;

    @Retry(
        maxRetries = 3,
        delay = 500,
        maxDuration = 5000,
        jitter = 100
    )
    @Timeout(5000)
    public ViaCEPResponse buscarEnderecoPorCEP(String CEP) {
        if (CEP == null || CEP.isBlank()) {
            logger.warn("CEP inválido fornecido");
            return null;
        }

        // Remover caracteres não numéricos
        String CEPLimpo = CEP.replaceAll("[^0-9]", "");

        if (CEPLimpo.length() != 8) {
            logger.warn("CEP deve conter exatamente 8 dígitos: " + CEP);
            return null;
        }

        Client client = null;
        try {
            client = ClientBuilder.newClient();
            
            String url = String.format("%s/%s/json", VIACEP_BASE_URL, CEPLimpo);
            
            logger.info("Consultando ViaCEP para CEP: " + CEP);

            ViaCEPResponse response = client.target(url)
                    .request()
                    .get(ViaCEPResponse.class);

            if (response != null && response.getErro() != null && response.getErro()) {
                logger.warn("CEP não encontrado: " + CEP);
                return null;
            }

            logger.info("Endereço encontrado para CEP: " + CEP);
            return response;

        } catch (Exception e) {
            logger.error("Erro ao consultar ViaCEP para CEP: " + CEP, e);
            return null;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
