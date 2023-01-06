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
		System.out.println("game " + id);
		sessions.put(id, session);
		broadcast(id, "User " + id + " joined");

	}

	@OnClose
	public void onClose(Session session, @PathParam("id") String id) {
		sessions.remove(id);
		broadcast(id, "User " + id + " left");
	}

	@OnError
	public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
		sessions.remove(id);
		broadcast(id, "User " + id + " left on error: " + throwable);
	}

	@OnMessage
	public void onMessage(String message, @PathParam("id") String id) {
		if (message.equalsIgnoreCase("_ready_")) {
			broadcast(id, "User " + id + " joined");
		} else {
			broadcast(id, ">> " + id + ": " + message);
		}
	}
	
	private void broadcast(String gameId, String message) {
		sessions.entrySet().stream().filter(entry -> entry.getKey().equals(gameId)).forEach(entry -> {
			entry.getValue().getAsyncRemote().sendObject(message, result -> {
				if (result.getException() != null) {
					System.out.println("Unable to send message: " + result.getException());
				}
			});
		});
	}

}
