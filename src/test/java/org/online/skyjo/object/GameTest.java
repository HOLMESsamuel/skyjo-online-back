package org.online.skyjo.object;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

	@Test
	void addPlayer() {
		Game game = new Game();
		Player player = new Player();

		game.addPlayer(player);

		assertAll(
				() -> assertEquals(1, game.getPlayers().size()),
				() -> assertEquals(player, game.getPlayers().get(0))
		);
	}

	@Test
	void addPLayers() {
		Game game = new Game();
		Player player = new Player();

		game.addPlayers(List.of(player, player));

		assertAll(
				() -> assertEquals(2, game.getPlayers().size()),
				() -> assertEquals(player, game.getPlayers().get(0))
		);
	}
}