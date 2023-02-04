package org.online.skyjo.batch;

import org.online.skyjo.object.Game;
import org.online.skyjo.rest.GameController;
import org.online.skyjo.websocket.GameWebsocket;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class RemoveOldGamesBatch {

	@Inject
	GameController gameController;

	@Inject
	GameWebsocket gameWebsocket;

	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(this::removeOldGames, 0, 10, TimeUnit.MINUTES);
	}

/**
 * This method removes all the games from the list of games which are older than 10 minutes.
 * The age of a game is calculated as the duration between the game's last modification date and current date time.
 * If the age of a game is greater than 10 minutes, it will be removed from the list of games and its associated websocket sessions.
 */
 protected void removeOldGames() {
		List<Game> games = gameController.getGames();
		for (int i = 0; i < games.size(); i++) {
			Game game = games.get(i);
			Duration age = Duration.between(game.getLastModificationDate(), LocalDateTime.now());
			if (age.toMinutes() > 10) {
				games.remove(i);
				gameWebsocket.getSessions().entrySet().removeIf(entry -> entry.getKey().contains(game.getId().toString()));
				i--;
			}
		}
	}
}
