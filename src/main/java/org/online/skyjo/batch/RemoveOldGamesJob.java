package org.online.skyjo.batch;

import io.quarkus.scheduler.Scheduled;
import org.online.skyjo.object.Game;
import org.online.skyjo.rest.GameController;
import org.online.skyjo.websocket.GameWebsocket;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.websocket.Session;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RemoveOldGamesJob implements Job {

	@Inject
	GameController gameController;

	@Inject
	GameWebsocket gameWebsocket;

	/**
	 * This method removes all the games from the list of games which are older than 10 minutes.
	 * The age of a game is calculated as the duration between the game's last modification date and current date time.
	 * If the age of a game is greater than 10 minutes, it will be removed from the list of games and its associated websocket sessions.
	 */
	protected void removeOldGames(List<Game> games,Map<String, Session> sessions) {
		for (int i = 0; i < games.size(); i++) {
			Game game = games.get(i);
			Duration age = Duration.between(game.getLastModificationDate(), LocalDateTime.now());
			if (age.toMinutes() > 10) {
				games.remove(i);
				sessions.entrySet().removeIf(entry -> entry.getKey().contains(game.getId().toString()));
				i--;
			}
		}
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			List<Game> games = gameController.getGames();
			Map<String, Session> sessions = gameWebsocket.getSessions();
			removeOldGames(games, sessions);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
}
