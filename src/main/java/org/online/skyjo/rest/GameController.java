package org.online.skyjo.rest;

import org.online.skyjo.object.Choice;
import org.online.skyjo.object.Coordinates;
import org.online.skyjo.object.Game;
import org.online.skyjo.object.Player;
import org.online.skyjo.service.BoardService;
import org.online.skyjo.service.DeckService;
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
    BoardService boardService;

    @Inject
    PlayerService playerService;

    @Inject
    DeckService deckService;

    @GET
    @Path("/{id}")
    public Response getGame(@PathParam("id") String id) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            return Response.ok(gameOptional.get()).build();
        }
        return GAME_NOT_EXISTS;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteGame(@PathParam("id") String id) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            games.remove(gameOptional.get());
            return Response.ok().build();
        }
        return GAME_NOT_EXISTS;
    }

    @PUT
    @Path("/{id}/new-game")
    public Response resetGame(@PathParam("id") String id) {
        Optional<Game> gameOptional = findGame(id);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            game.setDeck(deckService.initiateDeck());
            game.getPlayers().forEach(p -> playerService.resetPlayerForNextGame(p, game));
            game.setState(PREPARING);
            gameWebsocket.broadcastGame(game);
            return Response.ok(game).build();
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
                gameWebsocket.broadcastGame(game);
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
            if (RUNNING.equals(game.getState())){
                return GAME_ALREADY_STARTED;
            }
            if (game.getPlayers().size() > 7) {
                return TOO_MANY_PLAYERS;
            }
            game.addPlayer(playerService.initiatePlayer(playerName, game.getDeck()));
            gameWebsocket.broadcastGame(game);
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
                if(game.getPlayers().size() > 1 && game.getPlayers().stream().allMatch(p -> READY.equals(p.getState()))) {
                    game.setState(GAME_READY);
                }
                gameWebsocket.broadcastGame(game);
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
                gameWebsocket.broadcastGame(game);
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
                boardService.eliminateColumn(player.getBoard(), game.getDeck());
                if(player.getBoard().isVisible()) {
                    playerService.stateFinished(player);
                }
                gameService.nextPlayerTurn(game.getPlayers(), player);
                if(FINISH.equals(gameService.findCurrentPlayer(game.getPlayers()).getState())) {
                    game.setState(FINISH);
                    game.getPlayers().forEach(p -> p.setScore(p.getBoard().computeScore() + p.getScore()));
                }
                gameWebsocket.broadcastGame(game);
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
