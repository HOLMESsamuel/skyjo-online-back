package org.online.skyjo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;
import org.online.skyjo.object.RandomProvider;
import org.online.skyjo.service.DeckService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.online.skyjo.Constants.DECK_SIZE;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

	@Spy
	@InjectMocks
	DeckService deckService;

	@Mock
	private RandomProvider randomProvider;

	@Mock
	private Random random;

	@BeforeEach
	void init() {
		Mockito.lenient().when(randomProvider.getRandom()).thenReturn(random);
		Mockito.lenient().when(random.nextInt(anyInt())).thenReturn(0);
	}

	@Test
	void pickRandomCard() {
		Deck deck = new Deck();

		deckService.pickRandomCard(deck);

		assertEquals(149, deck.getCards().size());
	}

	/**
	 * In this case the deck service must refill the deck with the removed cards since we pick 151 cards in total.
	 */
	@Test
	void pickRandomCard_case_no_more_cards() {
		Deck deck = new Deck();
		ArrayList<Card> removedCards = new ArrayList<>(List.of(new Card(-3)));
		deck.setRemovedCards(removedCards);

		for (int i = 0; i < DECK_SIZE; i++) {
			deckService.pickRandomCard(deck);
		}

		assertEquals(-3, deckService.pickRandomCard(deck).getNumber());
	}

	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	void refillDeck() {
		Deck deck = new Deck();
		deck.setRemovedCards(new ArrayList<>(List.of(new Card(1))));

		deckService.refillDeck(deck);

		assertAll(
				() -> assertTrue(deck.getRemovedCards().isEmpty()),
				() -> assertEquals(1, deck.getCards().size()),
				() -> assertFalse(deck.getCards().get(0).isVisible())
		);

	}

	@Test
	void shuffleDeck() {
		Deck deck = new Deck();

		deckService.shuffleDeck(deck);

		assertTrue(true);
	}

	@Test
	void getLastCard() {
		Deck deck = new Deck();
		Card card = new Card(10);
		deck.getRemovedCards().add(card);

		assertAll(
				() -> assertEquals(card, deckService.getLastCard(deck)),
				() -> assertTrue(deck.getRemovedCards().isEmpty())
		);
	}

	@Test
	void getLastCard_empty() {
		Deck deck = new Deck();

		assertThrows(IndexOutOfBoundsException.class, () -> deckService.getLastCard(deck));
	}

	@Test
	void initiateDeck() {
		doNothing().when(deckService).shuffleDeck(any());

		Deck deck = deckService.initiateDeck();

		assertAll(
				() -> assertNotNull(deck.getRemovedCards()),
				() -> assertEquals(149, deck.getCards().size()),
				() -> assertTrue(deck.getRemovedCards().get(0).isVisible())
		);
	}

}