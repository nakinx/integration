package org.acme.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.dto.LoginRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class AuthResourceTest {

    @Test
    void testLogin_DeveRetornarTokenQuandoCredenciaisValidas() {
        LoginRequest request = new LoginRequest("admin@acme.org", "admin123");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
                .post("/auth/login")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("accessToken", notNullValue())
                .body("tokenType", is("Bearer"))
                .body("expiresIn", is(3600));
    }

    @Test
    void testLogin_DeveRetornar401QuandoSenhaInvalida() {
        LoginRequest request = new LoginRequest("admin@acme.org", "senhaerrada");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
                .post("/auth/login")
            .then()
                .statusCode(401)
                .body(is("Credenciais inválidas"));
    }

    @Test
    void testLogin_DeveRetornar401QuandoEmailNaoExiste() {
        LoginRequest request = new LoginRequest("naoexiste@acme.org", "admin123");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
                .post("/auth/login")
            .then()
                .statusCode(401)
                .body(is("Credenciais inválidas"));
    }

    @Test
    void testLogin_DeveRetornar400QuandoDadosInvalidos() {
        LoginRequest request = new LoginRequest("", "");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
                .post("/auth/login")
            .then()
                .statusCode(400);
    }

    @Test
    void testLogin_UsuarioComRoleUser() {
        LoginRequest request = new LoginRequest("user@acme.org", "user123");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
                .post("/auth/login")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("accessToken", notNullValue())
                .body("tokenType", is("Bearer"));
    }
}
