package org.acme.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClienteRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testClienteRequestValido() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setEmail("joao@example.com");
        request.setCep("01310100");

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty(), "Não deveria haver violações para dados válidos");
    }

    @Test
    void testClienteRequest_NomeVazio_DeveRetornarErro() {        
        ClienteRequest request = new ClienteRequest();
        request.setNome("");
        request.setEmail("joao@example.com");
        request.setCep("01310100");
        
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequest> violation = violations.iterator().next();
        assertEquals("Nome é obrigatório", violation.getMessage());
        assertEquals("nome", violation.getPropertyPath().toString());
    }

    @Test
    void testClienteRequest_NomeNulo_DeveRetornarErro() {        
        ClienteRequest request = new ClienteRequest();
        request.setNome(null);
        request.setEmail("joao@example.com");
        request.setCep("01310100");
        
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequest> violation = violations.iterator().next();
        assertEquals("Nome é obrigatório", violation.getMessage());
    }

    @Test
    void testClienteRequest_EmailVazio_DeveRetornarErro() {        
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setEmail("");
        request.setCep("01310100");
        
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequest> violation = violations.iterator().next();
        assertEquals("E-mail é obrigatório", violation.getMessage());
    }

    @Test
    void testClienteRequest_EmailInvalido_DeveRetornarErro() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setEmail("email-invalido");
        request.setCep("01310100");
        
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequest> violation = violations.iterator().next();
        assertEquals("E-mail deve ser válido", violation.getMessage());
    }

    @Test
    void testClienteRequest_CepVazio_DeveRetornarErro() {        
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setEmail("joao@example.com");
        request.setCep("");
        
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequest> violation = violations.iterator().next();
        assertEquals("CEP é obrigatório", violation.getMessage());
    }

    @Test
    void testClienteRequest_TodosCamposInvalidos_DeveRetornarMultiplosErros() {        
        ClienteRequest request = new ClienteRequest();
        request.setNome("");
        request.setEmail("email-invalido");
        request.setCep("");
        
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        
        assertEquals(3, violations.size());
    }

    @Test
    void testClienteRequest_Construtor() {
        ClienteRequest request = new ClienteRequest("João Silva", "joao@example.com", "01310100");
        
        assertEquals("João Silva", request.getNome());
        assertEquals("joao@example.com", request.getEmail());
        assertEquals("01310100", request.getCep());
    }

    @Test
    void testClienteRequest_GettersSetters() {        
        ClienteRequest request = new ClienteRequest();

        request.setNome("Maria Santos");
        request.setEmail("maria@example.com");
        request.setCep("04567890");
        
        assertEquals("Maria Santos", request.getNome());
        assertEquals("maria@example.com", request.getEmail());
        assertEquals("04567890", request.getCep());
    }
}
