package main.responses;

import main.processing.Player;
import main.requests.Request;

public class UnknownResponse extends Response {

	protected UnknownResponse() {}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		assert(false);
		setRecoAndResponseText(0, "Unknown action.");
	}
}
