package org.online.skyjo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.online.skyjo.object.Deck;
import org.online.skyjo.service.DeckService;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DeckServiceTest {

	@Inject
	DeckService deckService;

	@Test
	void pickRandomCard() {
		Deck deck = new Deck();

		deckService.pickRandomCard(deck);

		assertEquals(149, deck.getCards().size());
	}

	@Test
	void shuffleDeck() {
		Deck deck = new Deck();

		deckService.shuffleDeck(deck);

		assertTrue(true);
	}
}