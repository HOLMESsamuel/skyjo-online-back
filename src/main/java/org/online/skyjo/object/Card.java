package org.online.skyjo.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {
	private int number;
	private boolean visible;

	//true quand la carte est dans une colonne éliminée par la présence de trois cartes identiques
	private boolean eliminated;

	public Card(int number) {
		this.number = number;
	}
}
