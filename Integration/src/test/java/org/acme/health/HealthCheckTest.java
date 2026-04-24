package org.acme.health;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class HealthCheckTest {

    @Test
    public void testHealthCheckGeral_Retorna200() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks", notNullValue());
    }

    @Test
    public void testApplicationLivenessCheck_Retorna200() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/q/health/live")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks[0].name", equalTo("Aplicação"))
                .body("checks[0].status", equalTo("UP"))
                .body("checks[0].data.versão", equalTo("1.0.0"));
    }

    @Test
    public void testDatabaseReadinessCheck_Retorna200() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    public void testExternalApisHealthCheck_VerificaViaCepENominatim() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("checks.find { it.name == 'APIs Externas' }.status", 
                      equalTo("UP"));
    }

    @Test
    public void testHealthCheckIncluiDadosAdicionais() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("checks.find { it.name == 'Aplicação' }.data.status", 
                      equalTo("ativo"))
                .body("checks.find { it.name == 'Banco de Dados' }.data.banco", 
                      equalTo("PostgreSQL"));
    }

    @Test
    public void testHealthCheckLiveVsReady_DiferentesPropósitos() {
        long liveStart = System.currentTimeMillis();
        given()
                .when()
                .get("/q/health/live")
                .then()
                .statusCode(200);
        long liveTime = System.currentTimeMillis() - liveStart;

        long readyStart = System.currentTimeMillis();
        given()
                .when()
                .get("/q/health/ready")
                .then()
                .statusCode(200);
        long readyTime = System.currentTimeMillis() - readyStart;

        // Readiness pode ser mais lento pois verifica DB e APIs
        System.out.println("Live check: " + liveTime + "ms, Ready check: " + readyTime + "ms");
    }
}
