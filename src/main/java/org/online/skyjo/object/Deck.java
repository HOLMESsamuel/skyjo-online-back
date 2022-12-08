package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static org.online.skyjo.Constants.DECK_SIZE;

@Getter
@Setter
public class Deck {
	private List<Card> cards;

	/**
	 * Creates a deck with ten times each card with a value between -2 and 12.
	 */
	public Deck() {
		this.cards = new ArrayList<>(DECK_SIZE);
		for (int i = -2; i <= 12; i++) {
			for (int j = 0; j < 10; j++) {
				this.cards.add(new Card(i));
			}
		}
	}

}
