package org.online.skyjo.aspect.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.online.skyjo.object.Game;

/**
 * The event sent when a game is uploaded via the websocket.
 */
@Getter
@AllArgsConstructor
public class GameUploaded {

	private Game game;
}
