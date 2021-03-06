package main.responses;

import java.util.ArrayList;

import main.database.StatWindowRowDto;
import main.database.StatsDao;
import main.processing.Player;
import main.requests.Request;
import main.requests.ShowStatWindowRequest;
import main.types.Stats;

public class ShowStatWindowResponse extends Response {
	public ShowStatWindowResponse() {
		setAction("show_stat_window");
	}
	
	private int statId = 0;
	private ArrayList<StatWindowRowDto> rows = null;

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof ShowStatWindowRequest))
			return;
		
		ShowStatWindowRequest request = (ShowStatWindowRequest)req;
		Stats stat = Stats.withValue(request.getStatId());
		if (stat == null)
			return;
		
		statId = stat.getValue();
		
		// potentially null; expected behaviour (handled on the client side)
		rows = StatsDao.getStatWindowRows().get(Stats.HERBLORE);
		
		responseMaps.addClientOnlyResponse(player, this);
	}

}
