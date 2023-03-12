package org.online.skyjo.aspect.bot;

import org.online.skyjo.object.Player;
import org.online.skyjo.service.GameService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class BotInterceptor {

	@Inject
	GameService gameService;

	/**
	 * Listens for game uploaded event, if it is the turn of a bot make it play.
	 * @param upload the game upload event containing the game
	 * @throws InterruptedException
	 */
	public void intercept(@Observes GameUploaded upload) throws InterruptedException {
		Player currentPlayer = gameService.findCurrentPlayer(upload.getGame().getPlayers());
		if(currentPlayer != null && currentPlayer.isBot()) {
			gameService.botPlay(upload.getGame(), currentPlayer);
		}
	}

}
