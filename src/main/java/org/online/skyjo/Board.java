package org.online.skyjo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
	private int score;
	private Card[] grid = new Card[12];
}
