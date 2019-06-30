package main.responses;

import main.database.MineableDao;
import main.database.MineableDto;
import main.database.PlayerStorageDao;
import main.database.StatsDao;
import main.processing.FightManager;
import main.processing.PathFinder;
import main.processing.Player;
import main.processing.Player.PlayerState;
import main.requests.MineRequest;
import main.requests.Request;

public class MineResponse extends Response {
	public MineResponse() {
		setAction("mine");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof MineRequest))
			return;
		
		if (FightManager.fightWithFighterIsBattleLocked(player)) {
			setRecoAndResponseText(0, "you can't do that during combat.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		FightManager.cancelFight(player, responseMaps);
		
		MineRequest request = (MineRequest)req;
		
		if (!PathFinder.isNextTo(player.getTileId(), request.getTileId())) {
			player.setPath(PathFinder.findPath(player.getTileId(), request.getTileId(), false));
			player.setState(PlayerState.walking);
			player.setSavedRequest(req);
			return;
		} else {
			// does the tile have something mineable on it?
			MineableDto mineable = MineableDao.getMineableDtoByTileId(request.getTileId());
			if (mineable == null) {
				setRecoAndResponseText(0, "you can't mine this.");
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
			
			// TODO does player have a pickaxe in their inventory?
			
			// does the player have the level to mine this?
			if (StatsDao.getStatLevelByStatIdPlayerId(6, player.getId()) < mineable.getLevel()) {
				setRecoAndResponseText(0, String.format("you need %d mining to mine this.", mineable.getLevel()));
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
			
			// does player have inventory space
			if (PlayerStorageDao.getFreeSlotByPlayerId(player.getId()) == -1) {
				setRecoAndResponseText(0, "your inventory is too full to mine anymore.");
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
			
			new StartMiningResponse().process(request, player, responseMaps);
			
			player.setState(PlayerState.mining);
			player.setSavedRequest(req);
			player.setTickCounter(5);
		}
	}

}
