package main.responses;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import main.database.PlayerAnimationDto;
import main.processing.Player;
import main.requests.Request;
import main.types.PlayerPartType;


public class PlayerUpdateResponse extends Response {
	@Getter @Setter Integer id = null;
	@Setter String name = null;
	@Setter Integer combatLevel = null;
	@Setter Integer tileId = null;
	@Setter Integer damage = null;
	@Setter Integer damageType = null;
	@Setter Integer currentHp = null;
	@Setter Integer currentPrayer = null;
	@Setter Integer maxHp = null;
	@Setter Boolean loggedIn = null;
	@Setter Boolean snapToTile = null;
	@Setter Boolean respawn = null;
	@Setter private Map<PlayerPartType, PlayerAnimationDto> baseAnimations = null;
	@Setter private Map<PlayerPartType, PlayerAnimationDto> equipAnimations = null;

	public PlayerUpdateResponse() {
		setAction("player_update");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {

	}

}
