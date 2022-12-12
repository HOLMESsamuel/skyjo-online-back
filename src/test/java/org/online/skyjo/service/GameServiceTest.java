package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.Game;
import org.online.skyjo.object.Player;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@InjectMocks
	GameService gameService;

	@Test
	void initiateGame() {
		Player player = new Player();
		Game game = gameService.initiateGame(player);

		assertAll(
				() -> assertEquals(1, game.getPlayers().size()),
				() -> assertEquals(player, game.getPlayers().get(0))
		);
	}

}