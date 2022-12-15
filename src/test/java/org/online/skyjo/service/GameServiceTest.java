package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.Deck;
import org.online.skyjo.object.Game;
import org.online.skyjo.object.Player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@InjectMocks
	GameService gameService;

	@Mock
	DeckService deckService;

	@Mock
	PlayerService playerService;

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

}