package org.online.skyjo.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.online.skyjo.object.Game;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GameControllerTest {

    @Inject
    GameController gameController;


    @Test
    void getGame_null() {
        given()
                .when().get("/games/id")
                .then()
                .statusCode(404);
    }

    @Test
    void findGame() {
        Game game = gameController.createGame("testPlayer");
        UUID id = game.getId();

        assertAll(
                () -> assertTrue(gameController.findGame(id.toString()).isPresent()),
                () -> assertFalse(gameController.findGame("test").isPresent())
        );
    }
}