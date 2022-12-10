package org.online.skyjo.service;

import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;
import org.online.skyjo.object.RandomProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DeckService {

	@Inject
	RandomProvider randomProvider;

	public Card pickRandomCard(Deck deck) {
		List<Card> cards = deck.getCards();
		if(cards.isEmpty()) {
			refillDeck(deck);
			cards = deck.getCards();
		}
		int randomIndex = randomProvider.getRandom().nextInt(cards.size());
		Card card = cards.get(randomIndex);
		cards.remove(card);
		return card;
	}

	public void shuffleDeck(Deck deck) {
		Collections.shuffle(deck.getCards(), randomProvider.getRandom());
	}

	public void refillDeck(Deck deck) {
		deck.setCards(deck.getRemovedCards());
		deck.setRemovedCards(new ArrayList<>(0));

		shuffleDeck(deck);

		for(Card card : deck.getCards()) {
			card.setVisible(false);
		}
	}
}
