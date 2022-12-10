package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Game {
	private String id;
	private ArrayList<Player> players;
	private String state;
	private Deck deck;

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void addPLayers(List<Player> players) {
		for(Player player : players) {
			addPlayer(player);
		}
	}
}
