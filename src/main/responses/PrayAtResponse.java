package main.responses;

import main.database.SceneryDao;
import main.database.StatsDao;
import main.processing.PathFinder;
import main.processing.Player;
import main.processing.Player.PlayerState;
import main.requests.PrayAtRequest;
import main.requests.Request;
import main.types.Stats;

public class PrayAtResponse extends Response {
	public PrayAtResponse() {
		setAction("pray at");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof PrayAtRequest))
			return;
		
		PrayAtRequest request = (PrayAtRequest)req;
		int altarId = SceneryDao.getSceneryIdByTileId(player.getFloor(), request.getTileId());
		if (altarId == -1) {
			// there's no alter here, bail.
			return;
		}
		
		// if they mess with the client code and try to make them pray at some random scenery, fuck with them by making them walk to the scenery lmao whatta madlad
		if (!PathFinder.isNextTo(player.getFloor(), player.getTileId(), request.getTileId())) {
			player.setPath(PathFinder.findPath(player.getFloor(), player.getTileId(), request.getTileId(), false));
			player.setState(PlayerState.walking);
			player.setSavedRequest(req);
			return;
		} else if (altarId == 106) {// bit fucky but that's the sceneryId of the altar
			int maxPrayer = StatsDao.getStatLevelByStatIdPlayerId(Stats.PRAYER, player.getId());
			player.setPrayerPoints((float)maxPrayer, responseMaps);
			
			setResponseText("you recharge your prayer points.");
			responseMaps.addClientOnlyResponse(player, this);
		}
	}

}
