package org.online.skyjo.service;

import org.online.skyjo.object.Deck;
import org.online.skyjo.object.Game;
import org.online.skyjo.object.Player;
import org.online.skyjo.object.RandomProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.online.skyjo.Constants.*;

@ApplicationScoped
public class GameService {

	@Inject
	PlayerService playerService;

	@Inject
	DeckService deckService;

	@Inject
	RandomProvider randomProvider;

	public Game initiateGame(String playerName) {
		Game game = new Game();
		UUID uuid = UUID.randomUUID();
		Deck deck = deckService.initiateDeck();

		game.setId(uuid);
		game.setState(PREPARING);
		game.setPlayers(new ArrayList<>(List.of(playerService.initiatePlayer(playerName, deck))));
		game.setDeck(deck);

		return game;
	}

	/**
	 * Starts the game by changing its state and setting the players order.
	 * @param game game to start
	 */
	public void startGame(Game game) {
		ArrayList<Player> players = game.getPlayers();
		if(players.size() > 1 && players.stream().allMatch(p -> READY.equals(p.getState()))) {

			game.setPlayers(setPlayersOrder(players));

			game.getPlayers().get(0).setPlayerTurn(true);

			game.setState(RUNNING);
		}
	}

	/**
	 * Makes the first player in first position and shuffles the others.
	 * @param players the list of players
	 * @return a new player list with the first player in first
	 */
	protected ArrayList<Player> setPlayersOrder(ArrayList<Player> players) {
		Player firstPlayer = findFirstPlayer(players);
		players.remove(firstPlayer);
		Collections.shuffle(players, randomProvider.getRandom());

		ArrayList<Player> newPlayerList = new ArrayList<>();
		newPlayerList.add(firstPlayer);
		newPlayerList.addAll(players);

		return newPlayerList;
	}

	/**
	 * Finds the player with the highest board score to decide who begins.
	 * @param players list of players
	 * @return the player that will play first
	 */
	protected Player findFirstPlayer(ArrayList<Player> players) {
		Player firstPlayer = players.get(0);
		int max = firstPlayer.getBoard().computeScore();

		for (Player player : players) {
			int boardScore = player.getBoard().computeScore();
			if(boardScore > max) {
				max = boardScore;
				firstPlayer = player;
			}
		}

		return firstPlayer;
	}

	/**
	 * Manages the player turn boolean : only one player is playing at a time, if the current player was the last of the list
	 * the next one is the first of the list, otherwise it is the next in the list.
	 * @param players list of players
	 * @param currentPlayer the player that has just finished his turn
	 */
	public void nextPlayerTurn(List<Player> players, Player currentPlayer) {
		int currentPlayerIndex = players.indexOf(currentPlayer);
		if(currentPlayerIndex == players.size() - 1) {
			players.get(0).setPlayerTurn(true);
		} else {
			players.get(currentPlayerIndex + 1).setPlayerTurn(true);
		}
		currentPlayer.setPlayerTurn(false);
	}

	public Player findCurrentPlayer(List<Player> players) {
		return players.stream().filter(Player::isPlayerTurn).findFirst().get();
	}
}
