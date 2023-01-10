package org.online.skyjo.websocket;

import com.google.gson.Gson;
import org.online.skyjo.object.Game;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Class used on GameWebsocket to encode Game object to json.
 */
public class MessageEncoder implements Encoder.Text<Game> {

	private static final Gson gson = new Gson();

	@Override
	public String encode(Game game) {
		return gson.toJson(game);
	}

	@Override
	public void init(EndpointConfig endpointConfig) {
		// init the encoder
	}

	@Override
	public void destroy() {
		// Close resources
	}
}
