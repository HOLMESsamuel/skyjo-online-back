package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Game {
	private UUID id;
	private ArrayList<Player> players = new ArrayList<>();
	private String state;
	private Deck deck;
	private LocalDateTime lastModificationDate;

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void addPlayers(List<Player> players) {
		for(Player player : players) {
			addPlayer(player);
		}
	}
}
