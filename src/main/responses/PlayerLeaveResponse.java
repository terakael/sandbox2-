package main.responses;

import javax.websocket.Session;

import lombok.Getter;
import lombok.Setter;
import main.requests.Request;

public class PlayerLeaveResponse extends Response {
	
	@Getter @Setter private int id;
	@Setter private String name;
	
	public PlayerLeaveResponse(String action) {
		super(action);
	}

	@Override
	public ResponseType process(Request req, Session client) {
		return ResponseType.client_only;
	}

}