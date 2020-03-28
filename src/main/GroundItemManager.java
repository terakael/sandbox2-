package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.database.GroundTextureDao;
import main.processing.RoomGroundItemManager;
import main.processing.RoomGroundItemManager.GroundItem;

interface IGenericFunc {
	void handle(RoomGroundItemManager groundItemManager);
}

public class GroundItemManager {
	private static HashMap<Integer, RoomGroundItemManager> groundItemManagers = new HashMap<>();
	
	public static void initialize() {		
		for (int floor : GroundTextureDao.getDistinctFloors()) {
			RoomGroundItemManager groundItemManager = new RoomGroundItemManager(floor);
			groundItemManager.setupRespawnables();
			groundItemManagers.put(floor, groundItemManager);
		}
	}
	
	private static void allGroundItemManagers(IGenericFunc func) {
		for (Map.Entry<Integer, RoomGroundItemManager> entry : groundItemManagers.entrySet()) {
			func.handle(entry.getValue());
		}
	}
	
	public static boolean itemIsRespawnable(int floor, int tileId, int itemId) {
		if (!groundItemManagers.containsKey(floor))
			return false;
		
		return groundItemManagers.get(floor).itemIsRespawnable(tileId, itemId);
	}
	
	public static void process() {
		allGroundItemManagers(RoomGroundItemManager::process);
	}

	public static void add(int floor, int playerId, int itemId, int tileId, int count, int charges) {
		if (!groundItemManagers.containsKey(floor))
			return;
		
		groundItemManagers.get(floor).add(playerId, itemId, tileId, count, charges);
	}

	public static void remove(int floor, int playerId, int tileId, int itemId, int count, int charges) {
		if (!groundItemManagers.containsKey(floor))
			return;
		
		groundItemManagers.get(floor).remove(playerId, tileId, itemId, count, charges);
	}
	
	public static boolean itemIsOnGround(int floor, int playerId, int itemId) {		
		if (!groundItemManagers.containsKey(floor))
			return false;
		
		return groundItemManagers.get(floor).itemIsOnGround(playerId, itemId);
	}
	
	public static Map<Integer, List<Integer>> getItemIdsNearTile(int floor, int playerId, int tileId, int proximity) {
		if (!groundItemManagers.containsKey(floor))
			return new HashMap<>();
		
		return groundItemManagers.get(floor).getItemIdsNearTile(playerId, tileId, proximity);
	}
	
	public static GroundItem getItemAtTileId(int floor, int playerId, int itemId, int tileId) {
		if (!groundItemManagers.containsKey(floor))
			return null;
		
		return groundItemManagers.get(floor).getItemAtTileId(playerId, itemId, tileId);
	}
}
