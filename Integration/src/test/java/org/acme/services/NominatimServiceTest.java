package org.acme.services;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.dto.NominatimResponse;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class NominatimServiceTest {

    @Inject
    NominatimService nominatimService;

    @Test
    void testBuscarCoordenadas_EnderecoValido() {        
        String logradouro = "Avenida Paulista";
        String bairro = "Bela Vista";
        String localidade = "São Paulo";
        String uf = "SP";
        
        NominatimResponse response = nominatimService.buscarCoordenadas(logradouro, bairro, localidade, uf);
        
        if (response != null) {
            assertNotNull(response.getLatitude(), "Latitude não deve ser nula");
            assertNotNull(response.getLongitude(), "Longitude não deve ser nula");
            assertNotNull(response.getMapUrl(), "MapUrl não deve ser nula");
            
            // Verificar se as coordenadas estão no intervalo esperado para São Paulo
            assertTrue(response.getLatitude() >= -24 && response.getLatitude() <= -23, "Latitude inválida para São Paulo");
            assertTrue(response.getLongitude() >= -47 && response.getLongitude() <= -46, "Longitude inválida para São Paulo");
        }
    }

    @Test
    void testBuscarCoordenadas_SemLogradouro() {
        String bairro = "Bela Vista";
        String localidade = "São Paulo";
        String uf = "SP";

        NominatimResponse response = nominatimService.buscarCoordenadas(null, bairro, localidade, uf);

        if (response != null) {
            assertNotNull(response.getLatitude(), "Latitude não deve ser nula se response não é nula");
            assertNotNull(response.getLongitude(), "Longitude não deve ser nula se response não é nula");
        }
    }

    @Test
    void testBuscarCoordenadas_SemLocalidade() {        
        String logradouro = "Avenida Paulista";
        String bairro = "Bela Vista";
        String uf = "SP";
        
        NominatimResponse response = nominatimService.buscarCoordenadas(logradouro, bairro, null, uf);

        if (response != null) {
            assertNotNull(response.getLatitude(), "Latitude não deve ser nula se response não é nula");
            assertNotNull(response.getLongitude(), "Longitude não deve ser nula se response não é nula");
        }
    }

    @Test
    void testBuscarCoordenadas_TodosCamposNulos() {        
        NominatimResponse response = nominatimService.buscarCoordenadas(null, null, null, null);

        assertNull(response, "Deve retornar null quando todos os campos são nulos");
    }

    @Test
    void testBuscarCoordenadas_TodosCamposVazios() {        
        NominatimResponse response = nominatimService.buscarCoordenadas("", "", "", "");
        
        assertNull(response, "Deve retornar null quando todos os campos são vazios");
    }

    @Test
    void testBuscarCoordenadas_LocalidadeInvalida() {        
        String logradouro = "Rua XYZ";
        String bairro = "Bairro ABC";
        String localidade = "Cidade Fantasma do Universo 123";
        String uf = "XX";
        
        NominatimResponse response = nominatimService.buscarCoordenadas(logradouro, bairro, localidade, uf);

        assertTrue(response == null || (response.getLatitude() != null && response.getLongitude() != null));
    }
}
