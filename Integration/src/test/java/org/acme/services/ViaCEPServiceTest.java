package org.acme.services;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.dto.ViaCEPResponse;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ViaCEPServiceTest {

    @Inject
    ViaCEPService viaCEPService;

    @Test
    void testBuscarEnderecoPorCEP_CEPValido() {
        String cepValido = "01310100"; 

        ViaCEPResponse response = viaCEPService.buscarEnderecoPorCEP(cepValido);
        
        assertNotNull(response);
        assertTrue(response.getLogradouro().contains("Avenida") || response.getLogradouro().contains("Paulista"));
        assertEquals("São Paulo", response.getLocalidade());
        assertEquals("SP", response.getUf());
    }

    @Test
    void testBuscarEnderecoPorCEP_CEPInvalido() {
        String cepInvalido = "00000000";

        ViaCEPResponse response = viaCEPService.buscarEnderecoPorCEP(cepInvalido);

        if (response != null) {
            assertTrue(response.getErro() != null && response.getErro());
        }
    }

    @Test
    void testBuscarEnderecoPorCEP_CEPNulo() {
        ViaCEPResponse response = viaCEPService.buscarEnderecoPorCEP(null);
        
        assertNull(response);
    }

    @Test
    void testBuscarEnderecoPorCEP_CEPVazio() {
        ViaCEPResponse response = viaCEPService.buscarEnderecoPorCEP("");
        
        assertNull(response);
    }

    @Test
    void testBuscarEnderecoPorCEP_CEPComFormatacao() {
        String cepComFormatacao = "01310-100";

        ViaCEPResponse response = viaCEPService.buscarEnderecoPorCEP(cepComFormatacao);
        
        assertTrue(response == null || response.getLogradouro() != null);
    }
}
