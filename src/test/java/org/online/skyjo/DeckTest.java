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

	@Test
	void getLastCard() {
		Deck deck = new Deck();
		Card card = new Card(10);
		deck.getRemovedCards().add(card);

		assertAll(
				() -> assertEquals(card, deck.getLastCard()),
				() -> assertTrue(deck.getRemovedCards().isEmpty())
		);
		

	}

	@Test
	void getLastCard_empty() {
		Deck deck = new Deck();

		assertThrows(IndexOutOfBoundsException.class, deck::getLastCard);
	}

}