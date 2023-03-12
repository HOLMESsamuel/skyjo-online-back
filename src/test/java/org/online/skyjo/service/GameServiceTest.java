package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.online.skyjo.object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.online.skyjo.Constants.BOT_NAMES;
import static org.online.skyjo.Constants.PREPARING;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Spy
	@InjectMocks
	GameService gameService;

	@Mock
	DeckService deckService;

	@Mock
	PlayerService playerService;

	@Mock
	BoardService boardService;

	@Mock
	RandomProvider randomProvider;

	@Mock
	Random random;

	@Mock
	Board board1;

	@Mock
	Player player1;

	@Mock
	Board board2;

	@Mock
	Player player2;

	@Test
	void initiateGame() {
		String playerName = "name";
		Player player = new Player(playerName);
		Deck deck = new Deck();
		when(playerService.initiatePlayer(any(), any())).thenReturn(player);
		when(deckService.initiateDeck()).thenReturn(deck);
		Game game = gameService.initiateGame(playerName);

		assertAll(
				() -> verify(deckService).initiateDeck(),
				() -> verify(playerService).initiatePlayer(playerName, deck),
				() -> assertEquals(PREPARING, game.getState()),
				() -> assertEquals(1, game.getPlayers().size()),
				() -> assertEquals(playerName, game.getPlayers().get(0).getName())
		);
	}

	@Test
	void findFirstPLayer() {
		ArrayList<Player> playerList = createPlayerList();
		when(board2.computeVisibleScore()).thenReturn(12);
		when(board1.computeVisibleScore()).thenReturn(5);

		Player firstPlayer = gameService.findFirstPlayer(playerList);

		assertEquals(player2, firstPlayer);

	}

	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	void setPlayersOrder() {
		when(randomProvider.getRandom()).thenReturn(random);
		when(random.nextInt()).thenReturn(0);
		ArrayList<Player> playerList = createPlayerList();
		Player player2 = playerList.get(1);
		doReturn(player2).when(gameService).findFirstPlayer(playerList);

		ArrayList<Player> orderedPlayerList = gameService.setPlayersOrder(playerList);

		assertAll(
				() -> assertEquals(2, orderedPlayerList.size()),
				() -> assertEquals(player2, orderedPlayerList.get(0))
		);
	}

	@ParameterizedTest
	@CsvSource(value = {"0, 1", "1, 0"})
	void nextPlayerTurn(int currentPlayerIndex, int nextPlayerExpectedIndex) {
		List<Player> players = List.of(new Player(), new Player());

		Player currentPlayer = players.get(currentPlayerIndex);

		gameService.nextPlayerTurn(players, currentPlayer);

		assertAll(
				() -> assertTrue(players.get(nextPlayerExpectedIndex).isPlayerTurn())
		);
	}

	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	void chooseBotName() {
		when(randomProvider.getRandom()).thenReturn(random);
		when(random.nextInt(anyInt())).thenReturn(0);
		List<Player> players = createPlayerList();
		String botName = gameService.chooseBotName(BOT_NAMES, players);

		assertAll(
				() -> assertNotNull(botName)
		);
	}

	private ArrayList<Player> createPlayerList() {
		//setup player 1
		when(player1.getBoard()).thenReturn(board1);
		lenient().when(player1.getName()).thenReturn("player1");

		//setup player 2
		when(player2.getBoard()).thenReturn(board2);
		lenient().when(player2.getName()).thenReturn("player2");

		return new ArrayList<>(List.of(player1, player2));
	}


}