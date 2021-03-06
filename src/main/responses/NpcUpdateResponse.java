package main.responses;

import lombok.Setter;
import main.processing.NPC;
import main.processing.Player;
import main.requests.Request;

public class NpcUpdateResponse extends Response {
	@Setter private Integer instanceId = null;
	@Setter private Integer damage = null;
	@Setter private Integer damageType = null;
	@Setter private Integer hp = null;
	@Setter private Integer tileId = null;
	@Setter private Boolean snapToTile = null;
	
	public NpcUpdateResponse() {
		setAction("npc_update");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		
	}

}
