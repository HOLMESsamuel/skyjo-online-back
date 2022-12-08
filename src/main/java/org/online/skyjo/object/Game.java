package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Game {
	private String id;
	private ArrayList<Player> players;
	private String state;
	private Deck deck;
	private Card lastCard;
}
