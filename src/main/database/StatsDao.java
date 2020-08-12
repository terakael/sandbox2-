package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import main.database.entity.update.UpdatePlayerStatsEntity;
import main.processing.DatabaseUpdater;
import main.types.Stats;

public class StatsDao {
	private StatsDao() {}
	
	@Getter private static Map<Integer, String> cachedStats = null;
	@Getter private static HashMap<Integer, Integer> expMap = null;
	@Getter private static Map<Stats, ArrayList<StatWindowRowDto>> statWindowRows;
	
	private static Map<Integer, Map<Integer, StatsDto>> playerStatExpMap; // playerId, <statId, dto>
	
	static {
		expMap = new HashMap<>();
		expMap.put(1,  0);

		int cumulativeExp = 0;
		for (int i = 2; i <= 99; ++i) {
			cumulativeExp += (int)(i - 1 + 300 * Math.pow(2, (i - 1) / 7) / 4);
			expMap.put(i, cumulativeExp);
		}
		
		statWindowRows = new HashMap<>();
		statWindowRows.put(Stats.HERBLORE, getHerbloreStatWindowRows());
		
		cacheStats();
		cachePlayerStatExp();
	}
	
	private static void cachePlayerStatExp() {
		final String query = "select player_id, stat_id, exp, relative_boost from player_stats";
		
		playerStatExpMap = new HashMap<>();
		
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query);
		) {			
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					final int playerId = rs.getInt("player_id");
					final int statId = rs.getInt("stat_id");
					final double exp = rs.getDouble("exp");
					final int relativeBoost = rs.getInt("relative_boost");
					
					StatsDto dto = new StatsDto(playerId, statId, exp, relativeBoost);
					
					if (!playerStatExpMap.containsKey(playerId))
						playerStatExpMap.put(playerId, new HashMap<>());
					
					if (!playerStatExpMap.get(playerId).containsKey(statId))
						playerStatExpMap.get(playerId).put(statId, dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Double> getAllStatExpByPlayerId(int id) {
		if (!playerStatExpMap.containsKey(id))
			return null;
		
		Map<Integer, Double> stats = new HashMap<>();
		for (StatsDto dto : playerStatExpMap.get(id).values())
			stats.put(dto.getStatId(), dto.getExp());

		return stats;
	}
	
	public static int getStatLevelByStatIdPlayerId(Stats statId, int playerId) {
		if (!playerStatExpMap.containsKey(playerId))
			return -1;
		
		if (!playerStatExpMap.get(playerId).containsKey(statId.getValue()))
			return -1;
		
		return getLevelFromExp(playerStatExpMap.get(playerId).get(statId.getValue()).getExp().intValue());
	}
	
	public static void addExpToPlayer(int playerId, Stats statId, double exp) {
		if (!playerStatExpMap.containsKey(playerId))
			return;
		
		if (!playerStatExpMap.get(playerId).containsKey(statId.getValue()))
			return;
		
		// we want to set it in memory as well as updating the database, as we never read directly from the db
		playerStatExpMap.get(playerId).get(statId.getValue()).addExp(exp);
		
		DatabaseUpdater.enqueue(UpdatePlayerStatsEntity.builder()
				.playerId(playerId)
				.statId(statId.getValue())
				.exp(playerStatExpMap.get(playerId).get(statId.getValue()).getExp())
				.build());
	}
	
	private static void cacheStats() {
		final String query = "select id, short_name from stats";
		
		cachedStats = new HashMap<>();
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery()
		) {
			while (rs.next())
				cachedStats.put(rs.getInt("id"), rs.getString("short_name"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getStatIdByName(String stat) {
		for (Map.Entry<Integer, String> entry : StatsDao.cachedStats.entrySet()) {
			if (entry.getValue().equals(stat))
				return entry.getKey();
		}
		
		return -1;
	}
	
	public static String getStatShortNameByStatId(int id) {
		if (StatsDao.cachedStats.containsKey(id))
			return StatsDao.cachedStats.get(id);
		
		return null;
	}
	
	public static void setRelativeBoostByPlayerIdStatId(int playerId, Stats statId, int relativeBoost) {
		if (!playerStatExpMap.containsKey(playerId))
			return;
		
		if (!playerStatExpMap.get(playerId).containsKey(statId.getValue()))
			return;
		
		playerStatExpMap.get(playerId).get(statId.getValue()).setRelativeBoost(relativeBoost);
		DatabaseUpdater.enqueue(UpdatePlayerStatsEntity.builder()
				.playerId(playerId)
				.statId(statId.getValue())
				.relativeBoost(relativeBoost)
				.build());
	}
	
	public static HashMap<Stats, Integer> getRelativeBoostsByPlayerId(int playerId) {
		HashMap<Stats, Integer> relativeBoosts = new HashMap<>();
		
		if (!playerStatExpMap.containsKey(playerId))
			return relativeBoosts;
		
		for (StatsDto dto : playerStatExpMap.get(playerId).values())
			relativeBoosts.put(Stats.withValue(dto.getStatId()), dto.getRelativeBoost());
		
		return relativeBoosts;
	}
	
	public static int getCurrentHpByPlayerId(int id) {
		if (!playerStatExpMap.containsKey(id))
			return 0;
		
		StatsDto hpDto = playerStatExpMap.get(id).get(Stats.HITPOINTS.getValue());
		return getLevelFromExp(hpDto.getExp().intValue()) + hpDto.getRelativeBoost();
	}
	
	public static int getLevelFromExp(int exp) {
		for (Map.Entry<Integer, Integer> entry : expMap.entrySet()) {
			if (exp < entry.getValue())
				return entry.getKey() - 1;
		}
		return 99;
	}

	public static int getMaxHpByPlayerId(int id) {
		if (!playerStatExpMap.containsKey(id))
			return 0;
		
		StatsDto hpDto = playerStatExpMap.get(id).get(Stats.HITPOINTS.getValue());
		return getLevelFromExp(hpDto.getExp().intValue());
	}
	
	public static int getCombatLevelByPlayerId(int id) {
		Map<Integer, Double> stats = getAllStatExpByPlayerId(id);
		
		return getCombatLevelByStats(
			getLevelFromExp(stats.get(Stats.STRENGTH.getValue()).intValue()),
			getLevelFromExp(stats.get(Stats.ACCURACY.getValue()).intValue()),
			getLevelFromExp(stats.get(Stats.DEFENCE.getValue()).intValue()),
			getLevelFromExp(stats.get(Stats.AGILITY.getValue()).intValue()),
			getLevelFromExp(stats.get(Stats.HITPOINTS.getValue()).intValue()),
			getLevelFromExp(stats.get(Stats.MAGIC.getValue()).intValue())
		);
	}
	
	public static int getCombatLevelByStats(int str, int att, int def, int agil, int hp, int magic) {
		return (int)Math.ceil(((double)str + att) / 3) 
				+ (int)Math.ceil(((double)def + hp) / 4) 
				+ (int)Math.ceil(((double)magic) / 5);
	}
	
	public static int getCombatLevelByStats(HashMap<Stats, Integer> stats) {
		return getCombatLevelByStats(
				stats.get(Stats.STRENGTH),
				stats.get(Stats.ACCURACY),
				stats.get(Stats.DEFENCE),
				stats.get(Stats.AGILITY),
				stats.get(Stats.HITPOINTS),
				stats.get(Stats.MAGIC)
			);
	}
	
	private static ArrayList<StatWindowRowDto> getHerbloreStatWindowRows() {
		final String query = 
				"select level, resultItem.id as itemId, destItem.id as itemId2, srcItem.id as itemId3 from use_item_on_item " + 
				"inner join brewable on resulting_item_id=brewable.potion_id " + 
				"inner join items srcItem on srcItem.id = src_id " + 
				"inner join items destItem on destItem.id = dest_id " + 
				"inner join items resultItem on resultItem.id = potion_id " + 
				"order by level";
		
		ArrayList<StatWindowRowDto> dtos = new ArrayList<>();
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
		) {			
			while (rs.next()) {
				StatWindowRowDto statWindowRow = new StatWindowRowDto(rs.getInt("level"), rs.getInt("itemId"));
				statWindowRow.setItemId2(rs.getInt("itemId2"));
				statWindowRow.setItemId3(rs.getInt("itemId3"));
				dtos.add(statWindowRow);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return dtos;
	}
}
