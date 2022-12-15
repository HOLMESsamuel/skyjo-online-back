package org.online.skyjo.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Player {
	private int score;
	private String name;
	private Board board;
	private Card cardInHand;
	private String state;

	public Player(String name) {
		this.name = name;
	}

}
