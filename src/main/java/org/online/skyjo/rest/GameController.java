package org.online.skyjo.rest;

import org.online.skyjo.object.Game;
import org.online.skyjo.service.GameService;
import org.online.skyjo.service.PlayerService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.online.skyjo.Constants.GAME_NOT_EXISTS;
import static org.online.skyjo.Constants.PLAYER_ALREADY_EXISTS;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class GameController {

    private static final List<Game> games = new ArrayList<>();

    @Inject
    GameService gameService;

    @Inject
    PlayerService playerService;

    @GET
    @Path("/{id}")
    public Response getGame(@PathParam("id") UUID id) {
        Optional<Game> gameOptional = games.stream().filter(g -> g.getId().equals(id)).findFirst();
        if(gameOptional.isPresent()) {
            return Response.ok(gameOptional.get()).build();
        }
        return GAME_NOT_EXISTS;
    }

    @POST
    public Game createGame(String playerName) {
        Game game = gameService.initiateGame(playerName);
        games.add(game);
        return game;
    }

    @PUT
    @Path("join/{id}")
    public Response joinGame(@PathParam("id") UUID id, String playerName) {
        Optional<Game> gameOptional = games.stream().filter(g -> g.getId().equals(id)).findFirst();
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            if (game.getPlayers().stream().anyMatch(p -> playerName.equals(p.getName()))) {
                return PLAYER_ALREADY_EXISTS;
            }
            game.addPlayer(playerService.initiatePlayer(playerName, game.getDeck()));
            return Response.ok(game).build();
        }
        return GAME_NOT_EXISTS;
    }
}