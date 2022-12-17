package org.online.skyjo.service;

import org.online.skyjo.object.Deck;
import org.online.skyjo.object.Game;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GameService {

	@Inject
	PlayerService playerService;

	@Inject
	DeckService deckService;

	public Game initiateGame(String playerName) {
		Game game = new Game();
		UUID uuid = UUID.randomUUID();
		Deck deck = deckService.initiateDeck();

		game.setId(uuid);
		game.setPlayers(new ArrayList<>(List.of(playerService.initiatePlayer(playerName, deck))));
		game.setDeck(deck);

		return game;
	}
}
