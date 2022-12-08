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

	public Card(int number) {
		this.number = number;
	}
}
