package org.online.skyjo;

import javax.ws.rs.core.Response;

public class Constants {
	public static final int DECK_SIZE = 150;

	//game states
	public static final String FINISH = "finish";
	public static final String RUNNING = "running";

	public static final String PICK_FROM_DECK = "deck";
	public static final String PICK_FROM_REMOVED = "removed";
	public static final String DROP_AND_REVEAL = "drop";
	public static final String REPLACE_CARD = "replace";

	//player states
	public static final String READY = "ready";

	public static final int ROW_NUMBER = 4;
	public static final int LINE_NUMBER = 3;

	public static final Response GAME_NOT_EXISTS = Response.status(404, "This game does not exists").build();
	public static final Response PLAYER_NOT_EXISTS = Response.status(404, "This player does not exists").build();
	public static final Response PLAYER_ALREADY_EXISTS = Response.status(409, "A player already exists with this name.").build();
	public static final Response GAME_NOT_READY = Response.status(409, "The game is not ready to begin").build();

	private Constants() {

	}
}
