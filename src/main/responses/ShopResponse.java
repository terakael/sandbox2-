package main.responses;

import java.util.HashMap;

import lombok.Setter;
import main.FightManager;
import main.database.ShopDao;
import main.database.ShopDto;
import main.processing.GeneralStore;
import main.processing.NPC;
import main.processing.NPCManager;
import main.processing.PathFinder;
import main.processing.Player;
import main.processing.Player.PlayerState;
import main.processing.ShopManager;
import main.processing.Store;
import main.requests.Request;
import main.requests.ShopRequest;

public class ShopResponse extends Response {
	@Setter private HashMap<Integer, ShopDto> shopStock = new HashMap<>();
	@Setter private String shopName;
	
	public ShopResponse() {
		setAction("shop");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof ShopRequest))
				return;
		
		ShopRequest request = (ShopRequest)req;
		NPC npc = NPCManager.get().getNpcByInstanceId(request.getObjectId());// request tileid is the instnace id
		if (npc == null) {
			setRecoAndResponseText(0, "you cannot do that.");
			return;
		}
		
		if (FightManager.fightWithFighterIsBattleLocked(player)) {
			setRecoAndResponseText(0, "you can't do that during combat.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		FightManager.cancelFight(player, responseMaps);
		
		if (!PathFinder.isNextTo(player.getTileId(), npc.getTileId())) {
			player.setPath(PathFinder.findPath(player.getTileId(), npc.getTileId(), false));
			player.setState(PlayerState.walking);
			player.setSavedRequest(req);
			return;
		} else {
			Store shop = ShopManager.getShopByOwnerId(npc.getId());
			if (shop != null) {
				player.setShopId(shop.getShopId());
				shopStock = shop.getStock();
				shopName = ShopDao.getShopNameById(shop.getShopId());
				responseMaps.addClientOnlyResponse(player, this);
			}
		}
	}
}