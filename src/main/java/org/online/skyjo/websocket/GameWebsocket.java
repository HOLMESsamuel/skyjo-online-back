package org.online.skyjo.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/games/{id}")
@ApplicationScoped
public class GameWebsocket {

	Map<String, Session> sessions = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("id") String id) {
		sessions.put(id, session);
	}

	@OnClose
	public void onClose(Session session, @PathParam("id") String id) {
		sessions.remove(id);
		broadcastMessage(id);
	}

	@OnError
	public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
		sessions.remove(id);
		broadcastMessage(id);
	}

	@OnMessage
	public void onMessage(String message, @PathParam("id") String id) {
		if (message.equalsIgnoreCase("_ready_")) {
			broadcastMessage(id);
		} else {
			broadcastMessage(id);
		}
	}

	public void broadcastMessage(String gameId) {
		sessions.entrySet().stream().filter(entry -> entry.getKey().contains(gameId)).forEach(entry -> {
			entry.getValue().getAsyncRemote().sendObject(gameId, result -> {
				if (result.getException() != null) {
					System.out.println("Unable to send message: " + result.getException());
				}
			});
		});
	}

}
