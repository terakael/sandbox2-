package main.responses;

import main.processing.Player;
import main.requests.MineRequest;
import main.requests.Request;

public class StartMiningResponse extends Response {

	public StartMiningResponse() {
		setAction("start_mining");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof MineRequest))
			return;
		
		MineRequest request = (MineRequest)req;
		responseMaps.addClientOnlyResponse(player, this);
	}

}