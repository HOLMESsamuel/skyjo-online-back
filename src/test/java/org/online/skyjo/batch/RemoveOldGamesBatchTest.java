package org.online.skyjo.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.Game;
import org.online.skyjo.rest.GameController;
import org.online.skyjo.websocket.GameWebsocket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveOldGamesBatchTest {

	@InjectMocks
	RemoveOldGamesBatch batch;
	@Mock
	GameController gameController;
	@Mock
	GameWebsocket gameWebsocket;

	@Test
	void removeOldGames_removes_games_that_are_older_than_10_minutes() {
		List<Game> games = new ArrayList<>();
		Game oldGame = new Game();
		oldGame.setLastModificationDate(LocalDateTime.now().minusMinutes(11));
		oldGame.setState("old");
		Game youngGame = new Game();
		youngGame.setLastModificationDate(LocalDateTime.now().minusMinutes(5));
		youngGame.setState("young");
		games.add(oldGame);
		games.add(youngGame);
		when(gameController.getGames()).thenReturn(games);

		when(gameWebsocket.getSessions()).thenReturn(new HashMap<>());

		batch.removeOldGames();

		verify(gameController, times(1)).getGames();
		verify(gameWebsocket, times(1)).getSessions();

		assertEquals(1, games.size());
		assertEquals("young", games.get(0).getState());
	}

}