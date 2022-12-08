package org.online.skyjo;

import lombok.Getter;
import lombok.Setter;

import static org.online.skyjo.Constants.DECK_SIZE;

@Getter
@Setter
public class Deck {
	private Card[] cards;

	public Deck() {
		this.cards = new Card[DECK_SIZE];
	}
}
