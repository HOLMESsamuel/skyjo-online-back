package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
	private int score;
	private String name;
	private Board board;
	private Card cardInHand;
	private String state;

}
