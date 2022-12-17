package org.online.skyjo;

import org.junit.jupiter.api.Test;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;

import static org.junit.jupiter.api.Assertions.*;
import static org.online.skyjo.Constants.DECK_SIZE;

class DeckTest {

	@Test
	void deckConstructorTest() {
		Deck deck = new Deck();
		assertAll(
				() -> assertEquals(DECK_SIZE, deck.getCards().size()),
				() -> assertEquals(0, deck.getRemovedCards().size())
		);
	}

}