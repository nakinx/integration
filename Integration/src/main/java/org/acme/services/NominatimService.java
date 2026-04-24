package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.acme.dto.NominatimResponse;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class NominatimService {

    private static final Logger logger = Logger.getLogger(NominatimService.class);
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search";
    private static final int TIMEOUT_MS = 5000;

    @Retry(
        maxRetries = 3,
        delay = 500,
        maxDuration = 5000,
        jitter = 100
    )
    @Timeout(5000)
    public NominatimResponse buscarCoordenadas(String logradouro, String bairro, String localidade, String uf) {
        if ((logradouro == null || logradouro.isBlank()) && 
            (localidade == null || localidade.isBlank())) {
            logger.warn("Informações basicas para buscar coordenadas está incompleta");
            return null;
        }

        Client client = null;
        try {
            client = ClientBuilder.newClient();
            
            // Montar a query com os dados de endereco disponiveis
            StringBuilder query = new StringBuilder();
            if (bairro != null && !bairro.isBlank()) {
                if (query.length() > 0) query.append(", ");
                query.append(bairro);
            }
            if (localidade != null && !localidade.isBlank()) {
                if (query.length() > 0) query.append(", ");
                query.append(localidade);
            }
            if (uf != null && !uf.isBlank()) {
                if (query.length() > 0) query.append(", ");
                query.append(uf);
            }

            String encodedQuery = java.net.URLEncoder.encode(query.toString(), "UTF-8");
            String url = String.format("%s?q=%s&format=json&limit=1", NOMINATIM_BASE_URL, encodedQuery);
            
            logger.debug("Consultando Nominatim para: {}", query);
            logger.debug("URL da requisição: {}", url);

            String responseBody = client.target(url)
                    .request()
                    .header("User-Agent", "Integration-API/1.0")
                    .get(String.class);

            if (responseBody == null || responseBody.isBlank()) {
                logger.warn("Coordenadas não encontradas para: {}", query);
                return null;
            }

            // Parse JSON da resposta para extrair latitude e longitude
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> results = mapper.readValue(responseBody, List.class);

            if (results == null || results.isEmpty()) {
                logger.warn("Coordenadas não encontradas para: {}", query);
                return null;
            }

            // Pegar o primeiro resultado
            Map<String, Object> firstResult = results.get(0);
            NominatimResponse response = new NominatimResponse();
            response.setLatitude(Double.parseDouble(firstResult.get("lat").toString()));
            response.setLongitude(Double.parseDouble(firstResult.get("lon").toString()));
            
            // Gerar URL do mapa
            String mapUrl = String.format(
                "https://www.openstreetmap.org/?mlat=%s&mlon=%s&zoom=15",
                response.getLatitude(),
                response.getLongitude()
            );
            response.setMapUrl(mapUrl);
            
            logger.info("Coordenadas encontradas - Latitude: {}, Longitude: {}", 
                       response.getLatitude(), response.getLongitude());

            return response;

        } catch (Exception e) {
            logger.error("Erro ao consultar Nominatim", e);
            return null;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
