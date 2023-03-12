package org.online.skyjo.service;

import org.online.skyjo.object.*;
import org.online.skyjo.websocket.GameWebsocket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.online.skyjo.Constants.*;

@ApplicationScoped
public class GameService {

	@Inject
	PlayerService playerService;

	@Inject
	DeckService deckService;

	@Inject
	RandomProvider randomProvider;

	@Inject
	BoardService boardService;

	@Inject
	GameWebsocket gameWebsocket;

	@Inject
	AIService aiService;

	public Game initiateGame(String playerName) {
		Game game = new Game();
		UUID uuid = UUID.randomUUID();
		Deck deck = deckService.initiateDeck();

		game.setId(uuid);
		game.setState(PREPARING);
		game.setPlayers(new ArrayList<>(List.of(playerService.initiatePlayer(playerName, deck))));
		game.setDeck(deck);
		game.setLastModificationDate(LocalDateTime.now());

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

			game.setLastModificationDate(LocalDateTime.now());

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
		int max = firstPlayer.getBoard().computeVisibleScore();

		for (Player player : players) {
			int boardScore = player.getBoard().computeVisibleScore();
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
		Player nextPlayer;
		if(currentPlayerIndex == players.size() - 1) {
			nextPlayer = players.get(0);
		} else {
			nextPlayer = players.get(currentPlayerIndex + 1);
		}
		nextPlayer.setPlayerTurn(true);
		currentPlayer.setPlayerTurn(false);
	}

	public Player findCurrentPlayer(List<Player> players) {
		Optional<Player> optionalCurrentPlayer = players.stream().filter(Player::isPlayerTurn).findFirst();
		return optionalCurrentPlayer.orElse(null);
	}

	/**
	 * Choose a name from a bot in the list of names.
	 * Can not choose a name that is already taken.
	 */
	public String chooseBotName(List<String> botNames, List<Player> players) {
		String botName = botNames.get(randomProvider.getRandom().nextInt(botNames.size()));
		if(players.stream().anyMatch(p -> p.getName().equals(botName))) {
			return chooseBotName(botNames, players);
		}
		return botName;
	}

	/**
	 * Sets the player state to ready and checks if all players are ready to start the game.
	 * @param player the player that is ready
	 * @param game the game
	 * @param firstCardsCoordinates the coordinates of the first cards to reveal
	 */
	public void playerReady(Player player, Game game, Coordinates firstCardsCoordinates) {
		player.getBoard().revealCard(firstCardsCoordinates.getRowCard1(), firstCardsCoordinates.getLineCard1());
		player.getBoard().revealCard(firstCardsCoordinates.getRowCard2(), firstCardsCoordinates.getLineCard2());
		player.setState(READY);
		if(game.getPlayers().size() > 1 && game.getPlayers().stream().allMatch(p -> READY.equals(p.getState()))) {
			game.setState(GAME_READY);
		}
	}

	/**
	 * Resets the game for a new one.
	 * @param game the game to reset
	 */
	public void resetGame(Game game) {
		game.setDeck(deckService.initiateDeck());
		game.getPlayers().forEach(p -> playerService.resetPlayerForNextGame(p, game));
		game.setState(PREPARING);
	}

	/**
	 * Manages the player turn and the game state.
	 * @param player the player that has just played a card
	 * @param game the game
	 * @param choice the choice made by the player
	 */
	public void playerPlayCard(Player player, Game game, Choice choice) {
		playerService.playCard(player, choice.getChoiceString(), game.getDeck(), choice.getRow(), choice.getColumn());
		manageEndOfTurn(game, player);
	}

	public void addBot(Game game) {
		String botName = chooseBotName(BOT_NAMES, game.getPlayers());
		game.addPlayer(playerService.initiateBot(botName, game.getDeck()));
		if(game.getPlayers().size() > 1 && game.getPlayers().stream().allMatch(p -> READY.equals(p.getState()))) {
			game.setState(GAME_READY);
		}
	}

	public void botPlay(Game game, Player bot) throws InterruptedException {
		Choice choice = botChooseCard(game, bot);
		TimeUnit.SECONDS.sleep(3);
		aiService.placeOrDropCard(game, bot, choice);
		manageEndOfTurn(game, bot);
		gameWebsocket.broadcastGame(game);
	}

	protected Choice botChooseCard(Game game, Player bot) {
		Choice choice = aiService.chooseCard(game, bot);
		playerService.getCard(bot, choice.getChoiceString(), game.getDeck());
		return choice;
	}

	protected void manageEndOfTurn(Game game, Player player) {
		boardService.eliminateColumn(player.getBoard(), game.getDeck());
		if(player.getBoard().isVisible()) {
			playerService.stateFinished(player);
		}
		nextPlayerTurn(game.getPlayers(), player);
		Player currentPlayer = findCurrentPlayer(game.getPlayers());
		if(FINISH.equals(currentPlayer.getState())) {
			game.setState(FINISH);
			game.getPlayers().forEach(p -> p.setScore(p.getBoard().computeScore() + p.getScore()));
		}
	}

}
