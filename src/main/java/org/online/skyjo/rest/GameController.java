package org.online.skyjo.rest;

import org.online.skyjo.object.Choice;
import org.online.skyjo.object.Coordinates;
import org.online.skyjo.object.Game;
import org.online.skyjo.object.Player;
import org.online.skyjo.service.GameService;
import org.online.skyjo.service.PlayerService;
import org.online.skyjo.websocket.GameWebsocket;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.online.skyjo.Constants.*;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class GameController {

    private static final List<Game> games = new ArrayList<>();

    @Inject
    GameService gameService;

    @Inject
    GameWebsocket gameWebsocket;

    @Inject
    PlayerService playerService;

    @GET
    @Path("/{id}")
    public Response getGame(@PathParam("id") String id) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            return Response.ok(gameOptional.get()).build();
        }
        return GAME_NOT_EXISTS;
    }

    @GET
    @Path("/{id}/ready")
    public Response startGame(@PathParam("id") String id) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            gameService.startGame(game);
            if(RUNNING.equals(game.getState())) {
                return Response.ok(game).build();
            }
            return GAME_NOT_READY;
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
    public Response joinGame(@PathParam("id") String id, String playerName) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            if (game.getPlayers().stream().anyMatch(p -> playerName.equals(p.getName()))) {
                return PLAYER_ALREADY_EXISTS;
            }
            game.addPlayer(playerService.initiatePlayer(playerName, game.getDeck()));
            gameWebsocket.broadcastMessage(id);
            return Response.ok(game).build();
        }
        return GAME_NOT_EXISTS;
    }

    @PUT
    @Path("/{id}/{name}/ready")
    public Response playerReady(@PathParam("id") String id, @PathParam("name") String playerName, Coordinates firstCardsCoordinates) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Optional<Player> optionalPlayer = game.getPlayers().stream().filter(p -> p.getName().equals(playerName)).findFirst();
            if(optionalPlayer.isPresent()) {
                Player player = optionalPlayer.get();
                player.getBoard().revealCard(firstCardsCoordinates.getRowCard1(), firstCardsCoordinates.getLineCard1());
                player.getBoard().revealCard(firstCardsCoordinates.getRowCard2(), firstCardsCoordinates.getLineCard2());
                player.setState(READY);
                return Response.ok(game).build();
            }
            return PLAYER_NOT_EXISTS;
        }
        return GAME_NOT_EXISTS;
    }

    @PUT
    @Path("/{id}/{name}/hand")
    public Response playerChooseCard(@PathParam("id") String id, @PathParam("name") String playerName, Choice choice) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Optional<Player> optionalPlayer = game.getPlayers().stream().filter(p -> p.getName().equals(playerName)).findFirst();
            if(optionalPlayer.isPresent()) {
                Player player = optionalPlayer.get();
                playerService.getCard(player, choice.getChoiceString(), game.getDeck());
                return Response.ok(game).build();
            }
            return PLAYER_NOT_EXISTS;
        }
        return GAME_NOT_EXISTS;
    }

    @PUT
    @Path("/{id}/{name}/board")
    public Response playerPlayCard(@PathParam("id") String id, @PathParam("name") String playerName, Choice choice) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Optional<Player> optionalPlayer = game.getPlayers().stream().filter(p -> p.getName().equals(playerName)).findFirst();
            if(optionalPlayer.isPresent()) {
                Player player = optionalPlayer.get();
                playerService.playCard(player, choice.getChoiceString(), game.getDeck(), choice.getRow(), choice.getLine());
                return Response.ok(game).build();
            }
            return PLAYER_NOT_EXISTS;
        }
        return GAME_NOT_EXISTS;
    }

    @GET
    @Path("/{id}/final-score")
    public Response getFinalScore(@PathParam("id") String id) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            if(FINISH.equals(game.getState())) {
                for(Player player : game.getPlayers()) {
                    player.setScore(player.getBoard().computeScore());
                }
                Collections.sort(game.getPlayers());
                return Response.ok(game).build();
            }
        }
        return GAME_NOT_EXISTS;
    }

    /**
     * Tries to find a game within the list corresponding to the given id.
     * @param id id of the game we want to find
     * @return optional empty if there is no game, or the game if it exists
     */
    protected Optional<Game> findGame(String id) {
        return games.stream().filter(g -> g.getId().toString().equals(id)).findFirst();
    }
}
