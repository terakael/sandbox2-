package main.responses;

import java.util.ArrayList;

import main.database.ItemDao;
import main.database.MineableDao;
import main.database.PlayerStorageDao;
import main.database.SmithableDao;
import main.database.SmithableDto;
import main.database.StatsDao;
import main.processing.FightManager;
import main.processing.Player;
import main.processing.Player.PlayerState;
import main.requests.AddExpRequest;
import main.requests.Request;
import main.requests.RequestFactory;
import main.requests.SmithRequest;
import main.types.Items;
import main.types.Stats;
import main.types.StorageTypes;

public class SmithResponse extends Response {

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof SmithRequest))
			return;
		
		if (FightManager.fightWithFighterIsBattleLocked(player)) {
			setRecoAndResponseText(0, "you can't do that during combat.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		FightManager.cancelFight(player, responseMaps);
		
		SmithRequest smithRequest = (SmithRequest)req;
		
		// checks:
		// is player next to furnace
		
		// is item smithable
		SmithableDto dto = SmithableDao.getSmithableItemByItemId(smithRequest.getItemId());
		if (dto == null) {
			setRecoAndResponseText(0, "you can't smith that.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		// does player have level to smith
		int smithingLevel = StatsDao.getStatLevelByStatIdPlayerId(Stats.SMITHING, player.getId());
		if (smithingLevel < dto.getLevel()) {
			setRecoAndResponseText(0, String.format("you need %d smithing to smith that.", dto.getLevel()));
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		// does player have correct materials in inventory		
		if (!playerHasItemsInInventory(player.getId(), dto.getMaterial1(), dto.getCount1()) ||
			!playerHasItemsInInventory(player.getId(), dto.getMaterial2(), dto.getCount2()) ||
			!playerHasItemsInInventory(player.getId(), dto.getMaterial3(), dto.getCount3())) {
			setRecoAndResponseText(0, "you don't have the correct materials to smith that.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		new StartSmithResponse().process(req, player, responseMaps);
		player.setState(PlayerState.smithing);
		player.setSavedRequest(req);
		
		player.setTickCounter(5);
	}

	private boolean playerHasItemsInInventory(int playerId, int materialId, int count) {
		if (materialId == 0)
			return true;// item doesn't require this material
		
		int itemsInInventory = PlayerStorageDao.getStorageItemCountByPlayerIdItemIdStorageTypeId(playerId, materialId, StorageTypes.INVENTORY);
		if (itemsInInventory < count && materialId == Items.COAL_ORE.getValue()) {// coal is a special case; check coal storage
			itemsInInventory += PlayerStorageDao.getStorageItemCountByPlayerIdItemIdStorageTypeId(playerId, Items.COAL_ORE.getValue(), StorageTypes.FURNACE);
		}
		return itemsInInventory >= count;
	}
}
