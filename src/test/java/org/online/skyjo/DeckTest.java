package org.online.skyjo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.online.skyjo.Constants.DECK_SIZE;

class DeckTest {

	@Test
	void deckConstructorTest() {
		Deck deck = new Deck();

		assertEquals(DECK_SIZE, deck.getCards().length);
	}

}