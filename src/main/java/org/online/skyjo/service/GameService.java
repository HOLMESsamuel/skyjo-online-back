package org.online.skyjo.service;

import org.online.skyjo.object.Game;
import org.online.skyjo.object.Player;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GameService {

	@Inject
	PlayerService playerService;

	@Inject
	DeckService deckService;

	@Inject
	BoardService boardService;

	public Game initiateGame(Player player) {
		Game game = new Game();
		game.setPlayers(new ArrayList<>(List.of(player)));

		return game;
	}
}
