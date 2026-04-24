package org.acme.resources;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.business.ClienteBusiness;
import org.acme.business.ClienteBusiness.CepInvalidoException;
import org.acme.business.ClienteBusiness.ClienteNaoEncontradoException;
import org.acme.business.ClienteBusiness.EmailJaCadastradoException;
import org.acme.dto.ClienteRequest;
import org.acme.entities.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
class ClienteResourceTest {

    @InjectMock
    ClienteBusiness clienteBusiness;

    private Cliente cliente;
    private ClienteRequest clienteRequest;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@example.com");
        cliente.setCep("01310-100");
        cliente.setLogradouro("Avenida Paulista");
        cliente.setBairro("Bela Vista");
        cliente.setLocalidade("São Paulo");
        cliente.setUf("SP");
        cliente.setLatitude(-23.561414);
        cliente.setLongitude(-46.656559);

        clienteRequest = new ClienteRequest();
        clienteRequest.setNome("João Silva");
        clienteRequest.setEmail("joao@example.com");
        clienteRequest.setCep("01310-100");
    }

    @Test
    void testListar_DeveRetornarStatus200ComListaDeClientes() {       
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteBusiness.listar()).thenReturn(clientes);
        
        given()
            .when()
                .get("/clientes")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].id", is(1))
                .body("[0].nome", is("João Silva"))
                .body("[0].email", is("joao@example.com"));

        verify(clienteBusiness, times(1)).listar();
    }

    @Test
    void testObter_DeveRetornarStatus200ComCliente() {       
        when(clienteBusiness.obter(1L)).thenReturn(cliente);
        
        given()
            .when()
                .get("/clientes/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("nome", is("João Silva"))
                .body("email", is("joao@example.com"))
                .body("cep", is("01310-100"))
                .body("logradouro", is("Avenida Paulista"));

        verify(clienteBusiness, times(1)).obter(1L);
    }

    @Test
    void testObter_DeveRetornarStatus404QuandoClienteNaoExiste() {       
        when(clienteBusiness.obter(99L)).thenReturn(null);
        
        given()
            .when()
                .get("/clientes/99")
            .then()
                .statusCode(404);

        verify(clienteBusiness, times(1)).obter(99L);
    }

    @Test
    void testCriar_DeveRetornarStatus201ComClienteCriado() throws Exception {       
        when(clienteBusiness.criar(any(ClienteRequest.class))).thenReturn(cliente);
        
        given()
            .contentType(ContentType.JSON)
            .body(clienteRequest)
            .when()
                .post("/clientes")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("nome", is("João Silva"))
                .body("email", is("joao@example.com"));

        verify(clienteBusiness, times(1)).criar(any(ClienteRequest.class));
    }

    @Test
    void testCriar_DeveRetornarStatus409QuandoEmailJaCadastrado() throws Exception {
        when(clienteBusiness.criar(any(ClienteRequest.class)))
                .thenThrow(new EmailJaCadastradoException("E-mail já cadastrado no sistema"));

        given()
            .contentType(ContentType.JSON)
            .body(clienteRequest)
            .when()
                .post("/clientes")
            .then()
                .statusCode(409)
                .body(is("E-mail já cadastrado no sistema"));

        verify(clienteBusiness, times(1)).criar(any(ClienteRequest.class));
    }

    @Test
    void testCriar_DeveRetornarStatus400QuandoCepInvalido() throws Exception {
        when(clienteBusiness.criar(any(ClienteRequest.class)))
                .thenThrow(new CepInvalidoException("CEP inválido ou não encontrado"));

        given()
            .contentType(ContentType.JSON)
            .body(clienteRequest)
            .when()
                .post("/clientes")
            .then()
                .statusCode(400)
                .body(is("CEP inválido ou não encontrado"));

        verify(clienteBusiness, times(1)).criar(any(ClienteRequest.class));
    }

    @Test
    void testCriar_DeveRetornarStatus400QuandoDadosInvalidos() {
        ClienteRequest requestInvalido = new ClienteRequest();
        requestInvalido.setEmail("joao@example.com");

        given()
            .contentType(ContentType.JSON)
            .body(requestInvalido)
            .when()
                .post("/clientes")
            .then()
                .statusCode(400);
    }

    @Test
    void testAtualizar_DeveRetornarStatus200ComClienteAtualizado() throws Exception {       
        when(clienteBusiness.atualizar(eq(1L), any(ClienteRequest.class))).thenReturn(cliente);
        
        given()
            .contentType(ContentType.JSON)
            .body(clienteRequest)
            .when()
                .put("/clientes/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("nome", is("João Silva"))
                .body("email", is("joao@example.com"));

        verify(clienteBusiness, times(1)).atualizar(eq(1L), any(ClienteRequest.class));
    }

    @Test
    void testAtualizar_DeveRetornarStatus404QuandoClienteNaoExiste() throws Exception {       
        when(clienteBusiness.atualizar(eq(99L), any(ClienteRequest.class)))
                .thenThrow(new ClienteNaoEncontradoException("Cliente não encontrado"));
        
        given()
            .contentType(ContentType.JSON)
            .body(clienteRequest)
            .when()
                .put("/clientes/99")
            .then()
                .statusCode(404)
                .body(is("Cliente não encontrado"));

        verify(clienteBusiness, times(1)).atualizar(eq(99L), any(ClienteRequest.class));
    }

    @Test
    void testAtualizar_DeveRetornarStatus409QuandoEmailJaCadastrado() throws Exception {       
        when(clienteBusiness.atualizar(eq(1L), any(ClienteRequest.class)))
                .thenThrow(new EmailJaCadastradoException("E-mail já cadastrado para outro cliente"));
        
        given()
            .contentType(ContentType.JSON)
            .body(clienteRequest)
            .when()
                .put("/clientes/1")
            .then()
                .statusCode(409)
                .body(is("E-mail já cadastrado para outro cliente"));

        verify(clienteBusiness, times(1)).atualizar(eq(1L), any(ClienteRequest.class));
    }

    @Test
    void testDeletar_DeveRetornarStatus204QuandoClienteDeletado() throws Exception {       
        doNothing().when(clienteBusiness).deletar(1L);
        
        given()
            .when()
                .delete("/clientes/1")
            .then()
                .statusCode(204);

        verify(clienteBusiness, times(1)).deletar(1L);
    }

    @Test
    void testDeletar_DeveRetornarStatus404QuandoClienteNaoExiste() throws Exception {       
        doThrow(new ClienteNaoEncontradoException("Cliente não encontrado"))
                .when(clienteBusiness).deletar(99L);
        
        given()
            .when()
                .delete("/clientes/99")
            .then()
                .statusCode(404)
                .body(is("Cliente não encontrado"));

        verify(clienteBusiness, times(1)).deletar(99L);
    }
}
