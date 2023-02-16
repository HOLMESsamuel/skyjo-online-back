package org.online.skyjo.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.Game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RemoveOldGamesJobTest {

	@InjectMocks
	RemoveOldGamesJob batch;

	@Test
	void removeOldGames_young_games_not_deleted() {
		//prepare
		Game game1 = new Game();
		game1.setLastModificationDate(LocalDateTime.now());
		Game game2 = new Game();
		game2.setLastModificationDate(LocalDateTime.now());
		List<Game> games = new ArrayList<>();
		games.add(game1);
		games.add(game2);

		//test
		batch.removeOldGames(games, null);

		//verify
		assertEquals(2, games.size());
	}

	@Test
	void removeOldGames_old_games_deleted() {
		//prepare
		Game game1 = new Game();
		game1.setLastModificationDate(LocalDateTime.now().minusDays(1));
		Game game2 = new Game();
		game2.setLastModificationDate(LocalDateTime.now().minusDays(1));
		List<Game> games = new ArrayList<>();
		games.add(game1);
		games.add(game2);

		//test
		batch.removeOldGames(games, new HashMap<>());

		//verify
		assertEquals(0, games.size());
	}

}