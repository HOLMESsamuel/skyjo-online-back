package org.online.skyjo.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class GameControllerTest {


    @Test
    void getGame_null() {
        given()
                .when().get("/games/id")
                .then()
                .statusCode(404);
    }
}