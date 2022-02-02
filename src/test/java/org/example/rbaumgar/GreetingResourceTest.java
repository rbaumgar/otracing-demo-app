package org.example.rbaumgar;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testSayHelloEndpoint() {
        given()
                .when().get("/sayHello/test")
                .then()
                .statusCode(200)
                .body(is("hello: test"));
    }

    @Test
    public void testSayRemoteEndpoint() {
        given()
                .when().get("/sayRemote/test")
                .then()
                .statusCode(200)
                .body(is("hello: test from http://localhost:8081/"));
    }
    
    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("hello"));
    }

    @Test
    public void testChainEndpoint() {
        given()
                .when().get("/chain")
                .then()
                .statusCode(200)
                .body(is("chain -> hello: test"));
    }

}