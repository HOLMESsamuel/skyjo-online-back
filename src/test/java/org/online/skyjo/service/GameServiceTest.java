package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
				() -> assertEquals(1, game.getPlayers().size()),
				() -> assertEquals(playerName, game.getPlayers().get(0).getName())
		);
	}

	@Test
	void findFirstPLayer() {
		ArrayList<Player> playerList = createPlayerList();

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

	private ArrayList<Player> createPlayerList() {
		when(player1.getBoard()).thenReturn(board1);
		when(board1.computeScore()).thenReturn(10);
		when(player2.getBoard()).thenReturn(board2);
		when(board2.computeScore()).thenReturn(15);

		return new ArrayList<>(List.of(player1, player2));
	}


}