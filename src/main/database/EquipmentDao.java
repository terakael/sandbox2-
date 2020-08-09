package main.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.database.entity.delete.DeletePlayerEquipment;
import main.database.entity.insert.InsertPlayerEquipment;
import main.processing.DatabaseUpdater;
import main.types.EquipmentTypes;
import main.types.Items;
import main.types.PlayerPartType;

public class EquipmentDao {
	private EquipmentDao() {};
	
	private static HashMap<Integer, EquipmentTypes> equipmentByType = new HashMap<>();
	private static HashMap<Integer, EquipmentDto> equipment = new HashMap<>();
	private static HashMap<Items, Items> reinforcedToBase = new HashMap<>();// reinforced_id, base_id
	
	private static Map<Integer, HashMap<Integer, Integer>> playerEquipment; // playerId, <equipmentId, slot>
	
//	private static Map<Integer, String> dynamicallyGeneratedSpriteMaps = null;
//	private static Map<Integer, List<Integer>> equipmentBySpriteMap = null;
	
	public static void setupCaches() {
		cacheEquipmentByType();
		cacheEquipment();
		cacheReinforcedtoBaseMap();
		cachePlayerEquipment();
	}
	
	private static void cacheEquipment() {
//		final String query = "select item_id, player_part_id, up_id, down_id, left_id, right_id, attack_left_id, attack_right_id, requirement, acc, str, def, agil, mage, hp, speed from equipment";
		final String query = "select item_id, player_part_id, animation_id, color, requirement, acc, str, def, agil, mage, hp, speed from equipment";
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query)
		) {
			Map<List<Integer>, PlayerAnimationDto> dynamicallyGeneratedAnimations = new HashMap<>();
			
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					
					if (!dynamicallyGeneratedAnimations.containsKey(Arrays.asList(rs.getInt("animation_id"), rs.getInt("color")))) {
						AnimationDto originalAnimation = AnimationDao.getAnimationDtoById(rs.getInt("animation_id"));
		
						PlayerAnimationDto animation;
						if (rs.getInt("color") > 0) {
							// create a new sprite map
							SpriteMapDto originalSpriteMap = SpriteMapDao.getSpriteMapDataById(originalAnimation.getSpriteMapId());
							String tintedBase64 = SpriteMapDao.tintImage(originalSpriteMap.getDataBase64(), rs.getInt("color"));
							int newSpriteMapId = SpriteMapDao.addSpriteMap("", tintedBase64);
							
							// create new sprite frames
							animation = new PlayerAnimationDto(
								SpriteFrameDao.addSpriteFrame(newSpriteMapId, SpriteFrameDao.getSpriteFrameDtoById(originalAnimation.getUpId())),
								SpriteFrameDao.addSpriteFrame(newSpriteMapId, SpriteFrameDao.getSpriteFrameDtoById(originalAnimation.getDownId())),
								SpriteFrameDao.addSpriteFrame(newSpriteMapId, SpriteFrameDao.getSpriteFrameDtoById(originalAnimation.getLeftId())),
								SpriteFrameDao.addSpriteFrame(newSpriteMapId, SpriteFrameDao.getSpriteFrameDtoById(originalAnimation.getRightId())),
								SpriteFrameDao.addSpriteFrame(newSpriteMapId, SpriteFrameDao.getSpriteFrameDtoById(originalAnimation.getAttackLeftId())),
								SpriteFrameDao.addSpriteFrame(newSpriteMapId, SpriteFrameDao.getSpriteFrameDtoById(originalAnimation.getAttackRightId()))
							);
						} else {
							animation = new PlayerAnimationDto(
									originalAnimation.getUpId(),
									originalAnimation.getDownId(),
									originalAnimation.getLeftId(),
									originalAnimation.getRightId(),
									originalAnimation.getAttackLeftId(),
									originalAnimation.getAttackRightId()
							);
						}
						
						dynamicallyGeneratedAnimations.put(Collections.unmodifiableList(Arrays.asList(rs.getInt("animation_id"), rs.getInt("color"))), animation);
					}
					
					
					int itemId = rs.getInt("item_id");
					equipment.put(itemId, 
								new EquipmentDto(itemId, 
												 rs.getInt("player_part_id"), 
												 rs.getInt("requirement"), 
												 getEquipmentTypeByEquipmentId(itemId),
//												 new PlayerAnimationDto(rs.getInt("up_id"), rs.getInt("down_id"), rs.getInt("left_id"), rs.getInt("right_id"), rs.getInt("attack_left_id"), rs.getInt("attack_right_id")),
												 dynamicallyGeneratedAnimations.get(Arrays.asList(rs.getInt("animation_id"), rs.getInt("color"))),
												 new EquipmentBonusDto(rs.getInt("acc"), rs.getInt("str"), rs.getInt("def"), rs.getInt("agil"), rs.getInt("mage"), rs.getInt("hp"), rs.getInt("speed"))));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void cacheEquipmentByType() {
		final String query = 
				"select equipment_types.id, equipment.item_id from equipment" + 
				" inner join equipment_types on equipment.equipment_type_id = equipment_types.id";
		
		try (
				Connection connection = DbConnection.get();
				PreparedStatement ps = connection.prepareStatement(query)
			) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						EquipmentTypes type = EquipmentTypes.withValue(rs.getInt("id"));
						if (type != null)
							equipmentByType.put(rs.getInt("item_id"), type);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	private static void cacheReinforcedtoBaseMap() {
		reinforcedToBase.put(Items.REINFORCED_COPPER_HELMET, Items.COPPER_HELMET);
		reinforcedToBase.put(Items.REINFORCED_COPPER_PLATEBODY, Items.COPPER_PLATEBODY);
		reinforcedToBase.put(Items.REINFORCED_COPPER_PLATELEGS, Items.COPPER_PLATELEGS);
		reinforcedToBase.put(Items.REINFORCED_COPPER_SHIELD, Items.COPPER_SHIELD);
		
		reinforcedToBase.put(Items.REINFORCED_IRON_HELMET, Items.IRON_HELMET);
		reinforcedToBase.put(Items.REINFORCED_IRON_PLATEBODY, Items.IRON_PLATEBODY);
		reinforcedToBase.put(Items.REINFORCED_IRON_PLATELEGS, Items.IRON_PLATELEGS);
		reinforcedToBase.put(Items.REINFORCED_IRON_SHIELD, Items.IRON_SHIELD);
		
		reinforcedToBase.put(Items.REINFORCED_STEEL_HELMET, Items.STEEL_HELMET);
		reinforcedToBase.put(Items.REINFORCED_STEEL_PLATEBODY, Items.STEEL_PLATEBODY);
		reinforcedToBase.put(Items.REINFORCED_STEEL_PLATELEGS, Items.STEEL_PLATELEGS);
		reinforcedToBase.put(Items.REINFORCED_STEEL_SHIELD, Items.STEEL_SHIELD);
		
		reinforcedToBase.put(Items.REINFORCED_MITHRIL_HELMET, Items.MITHRIL_HELMET);
		reinforcedToBase.put(Items.REINFORCED_MITHRIL_PLATEBODY, Items.MITHRIL_PLATEBODY);
		reinforcedToBase.put(Items.REINFORCED_MITHRIL_PLATELEGS, Items.MITHRIL_PLATELEGS);
		reinforcedToBase.put(Items.REINFORCED_MITHRIL_SHIELD, Items.MITHRIL_SHIELD);
		
		reinforcedToBase.put(Items.REINFORCED_ADDY_HELMET, Items.ADDY_HELMET);
		reinforcedToBase.put(Items.REINFORCED_ADDY_PLATEBODY, Items.ADDY_PLATEBODY);
		reinforcedToBase.put(Items.REINFORCED_ADDY_PLATELEGS, Items.ADDY_PLATELEGS);
		reinforcedToBase.put(Items.REINFORCED_ADDY_SHIELD, Items.ADDY_SHIELD);
		
		reinforcedToBase.put(Items.REINFORCED_RUNE_HELMET, Items.RUNE_HELMET);
		reinforcedToBase.put(Items.REINFORCED_RUNE_PLATEBODY, Items.RUNE_PLATEBODY);
		reinforcedToBase.put(Items.REINFORCED_RUNE_PLATELEGS, Items.RUNE_PLATELEGS);
		reinforcedToBase.put(Items.REINFORCED_RUNE_SHIELD, Items.RUNE_SHIELD);
	}
	
	public static int getBaseItemFromReinforcedItem(int reinforcedId) {
		Items reinforcedItem = Items.withValue(reinforcedId);
		if (reinforcedItem == null)
			return 0;
		
		if (reinforcedToBase.containsKey(reinforcedItem))
			return reinforcedToBase.get(reinforcedItem).getValue();
		
		return 0;
	}
	
	public static EquipmentTypes getEquipmentTypeByEquipmentId(int equipmentId) {
		if (equipmentByType.containsKey(equipmentId))
			return equipmentByType.get(equipmentId);
		return null;
	}
	
	public static Set<Integer> getEquippedSlotsByPlayerId(int playerId) {
		if (!playerEquipment.containsKey(playerId))
			return new HashSet<>();
		
		return new HashSet<>(playerEquipment.get(playerId).values());
	}
	
	public static Map<Integer, Integer> getEquippedSlotsAndItemIdsByPlayerId(int playerId) {
		if (!playerEquipment.containsKey(playerId))
			return new HashMap<>();
		
		return playerEquipment.get(playerId);
	}

	public static boolean isSlotEquipped(int playerId, int slot) {
		if (!playerEquipment.containsKey(playerId))
			return false;
		
		return playerEquipment.get(playerId).values().contains(slot);
	}

	public static void clearEquippedItem(int playerId, int slot) {
		int equippedItemIdToRemove = -1;
		if (playerEquipment.containsKey(playerId)) {
			for (Map.Entry<Integer, Integer> entry : playerEquipment.get(playerId).entrySet()) {
				if (entry.getValue() == slot) {
					equippedItemIdToRemove = entry.getKey();
					break;
				}
			}
		}
		
		if (equippedItemIdToRemove != -1) {
			playerEquipment.get(playerId).remove(equippedItemIdToRemove);
			DatabaseUpdater.enqueue(DeletePlayerEquipment.builder().playerId(playerId).equipmentId(equippedItemIdToRemove).build());
		}
	}
	
	public static void setEquippedItem(int playerId, int slot, int itemId) {
		if (!playerEquipment.containsKey(playerId))
			playerEquipment.put(playerId, new HashMap<>());
		
		if (!playerEquipment.get(playerId).containsKey(itemId))
			playerEquipment.get(playerId).put(itemId, slot);
		
		DatabaseUpdater.enqueue(InsertPlayerEquipment.builder().playerId(playerId).equipmentId(itemId).slot(slot).build());
	}
	
	public static EquipmentDto getEquipmentByItemId(int itemId) {
		if (equipment.containsKey(itemId))
			return equipment.get(itemId);
		return null;
	}
	
	public static int getWeaponIdByPlayerId(int playerId) {
		if (!playerEquipment.containsKey(playerId))
			return 0;
		
		for (int equipmentId : playerEquipment.get(playerId).keySet()) {
			if (equipment.get(equipmentId).getPartId() == PlayerPartType.ONHAND.getValue()) {
				return equipmentId;
			}
		}
		
		return 0;
	}
	
	public static void clearEquippedItemByPartId(int playerId, int partId) {
		if (!playerEquipment.containsKey(playerId))
			return;
		
		int equipmentIdToRemove = -1;
		for (int equipmentId : playerEquipment.get(playerId).keySet()) {
			if (equipment.get(equipmentId).getPartId() == partId) {
				equipmentIdToRemove = equipmentId;
				break;
			}
		}
		
		if (equipmentIdToRemove != -1) {
			playerEquipment.get(playerId).remove(equipmentIdToRemove);
			DatabaseUpdater.enqueue(DeletePlayerEquipment.builder().playerId(playerId).equipmentId(equipmentIdToRemove).build());
		}
	}
	
	public static void clearAllEquppedItems(int playerId) {
		if (playerEquipment.containsKey(playerId))
			playerEquipment.remove(playerId);
		
		DatabaseUpdater.enqueue(DeletePlayerEquipment.builder().playerId(playerId).build());
	}

	public static boolean isItemEquippedByItemIdAndSlot(int playerId, int itemId, int slot) {
		if (!playerEquipment.containsKey(playerId))
			return false;
		
		if (!playerEquipment.get(playerId).containsKey(itemId))
			return false;
		
		return playerEquipment.get(playerId).get(itemId) == slot;
	}
	
	public static EquipmentBonusDto getEquipmentBonusesByPlayerId(int playerId) {
		EquipmentBonusDto totalBonuses = new EquipmentBonusDto(0, 0, 0, 0, 0, 0, 0);
		
		if (!playerEquipment.containsKey(playerId))
			return totalBonuses;
		
		for (int equipmentId : playerEquipment.get(playerId).keySet()) {
			if (!equipment.containsKey(equipmentId))
				continue;
			totalBonuses.add(equipment.get(equipmentId).getBonuses());
		}
		
		return totalBonuses;
	}
	
	public static void cachePlayerEquipment() {
		playerEquipment = new HashMap<>();
		
		final String query = "select player_id, equipment_id, slot from player_equipment";
		
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query)
		) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					final int playerId = rs.getInt("player_id");
					final int slot = rs.getInt("slot");
					final int equipmentId = rs.getInt("equipment_id");
					if (!playerEquipment.containsKey(playerId))
						playerEquipment.put(playerId, new HashMap<>());
					playerEquipment.get(playerId).put(equipmentId, slot);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<PlayerPartType, PlayerAnimationDto> getEquipmentAnimationsByPlayerId(int playerId) {
		Map<PlayerPartType, PlayerAnimationDto> animationMap = new HashMap<>();
		
		if (!playerEquipment.containsKey(playerId))
			return animationMap;
		
		for (int equipmentId : playerEquipment.get(playerId).keySet()) {
			EquipmentDto dto = equipment.get(equipmentId);
			animationMap.put(PlayerPartType.withValue(dto.getPartId()), dto.getAnimations());
		}
		
		return animationMap;
	}
	
} 