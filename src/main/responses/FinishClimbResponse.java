package main.responses;

import main.database.AnimationDao;
import main.database.LadderConnectionDao;
import main.database.LadderConnectionDto;
import main.database.StatsDao;
import main.processing.FightManager;
import main.processing.PathFinder;
import main.processing.Player;
import main.processing.Player.PlayerState;
import main.requests.ClimbRequest;
import main.requests.Request;
import main.types.Stats;

public class FinishClimbResponse extends Response {

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof ClimbRequest))
			return;
		
		if (FightManager.fightWithFighterExists(player)) {
			setRecoAndResponseText(0, "you can't do that during combat.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		ClimbRequest request = (ClimbRequest)req;
		
		if (!PathFinder.isNextTo(player.getFloor(), player.getTileId(), request.getTileId())) {
			player.setPath(PathFinder.findPath(player.getFloor(), player.getTileId(), request.getTileId(), false));
			player.setState(PlayerState.walking);
			player.setSavedRequest(req);
			return;
		} else {
			for (LadderConnectionDto dto : LadderConnectionDao.getLadderConnections()) {
				if (dto.getFromFloor() == player.getFloor() && dto.getFromTileId() == request.getTileId()) {
					player.setTileId(dto.getToTileId());
					player.setFloor(dto.getToFloor());
					player.clearPath();

					if (dto.getFromFloor() != dto.getToFloor()) // if the room changes we need to reload the room
						new LoadRoomResponse().process(null, player, responseMaps);
					
					// local players will receive a player_update message regardless, but the current player will not receive one for himself automatically.
					PlayerUpdateResponse playerUpdate = new PlayerUpdateResponse();
					playerUpdate.setId(player.getId());
					playerUpdate.setTileId(player.getTileId());
					playerUpdate.setSnapToTile(true);
					responseMaps.addClientOnlyResponse(player, playerUpdate);
					
					break;
				}
			}
			player.setState(PlayerState.idle);
		}
	}

}
