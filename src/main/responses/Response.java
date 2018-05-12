package main.responses;

import javax.websocket.Session;

import com.google.gson.Gson;

import lombok.Getter;
import main.requests.Request;

public abstract class Response {
	protected static Gson gson = new Gson();
	
	public enum ResponseType {
		broadcast,
		client_only,
		no_response
	};
	
	@Getter
	private int success = 1;
	private String responseText = "";
	private String action;
	
	public Response(String action) {
		this.action = action;
	}

	public void setRecoAndResponseText(int success, String responseText) {
		this.success = success;
		this.responseText = responseText;
	}

	public abstract ResponseType process(Request req, Session client);

}
