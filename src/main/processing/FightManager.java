package main.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import main.database.StatsDao;
import main.responses.PlayerUpdateResponse;
import main.responses.PvmEndResponse;
import main.responses.PvpEndResponse;
import main.responses.ResponseMaps;
import main.types.DamageTypes;
import main.types.Prayers;
import main.types.Stats;
import main.utils.RandomUtil;

public class FightManager {
	private FightManager() {}
	
	private static ArrayList<Fight> fights = new ArrayList<>();
	
	public static class Fight {
		@Getter private Attackable fighter1;
		@Getter private Attackable fighter2;
		@Getter private int battleLockTicks;
		
		Fight(Attackable fighter1, Attackable fighter2, boolean fighter1initiated) {
			battleLockTicks = 10;
			
			this.fighter1 = fighter1;
			this.fighter2 = fighter2;
			
			this.fighter1.setTarget(this.fighter2);
			this.fighter2.setTarget(this.fighter1);
			
			this.fighter1.setCooldown(fighter1initiated ? 0 : 3);
			this.fighter2.setCooldown(fighter1initiated ? 3 : 0);
		}
		
		public boolean isBattleLocked() {
			return battleLockTicks > 0;
		}
		
		public boolean process(ResponseMaps responseMaps) {
			// if the players aren't on the same tile then they are still closing in on eachother
			if (!PathFinder.isNextTo(fighter1.getFloor(), fighter1.getTileId(), fighter2.getTileId())) {
				return false;
			}
			
			if (battleLockTicks > 0) {
				if (--battleLockTicks < 0)
					battleLockTicks = 0;
			}
			
			if (fighter1.readyToHit()) {
				boolean fighter2Dead = processHit(fighter1, fighter2, responseMaps);
				if (fighter2Dead) {
					fighter1.onKill(fighter2, responseMaps);
					fighter2.onDeath(fighter1, responseMaps);
					return true;
				}
			}
			
			if (fighter2.readyToHit()) {
				boolean fighter1Dead = processHit(fighter2, fighter1, responseMaps);
				if (fighter1Dead) {
					fighter2.onKill(fighter1, responseMaps);
					fighter1.onDeath(fighter2, responseMaps);
					return true;
				}
			}
			return false;
		}
		
		private boolean processHit(Attackable attackable, Attackable other, ResponseMaps responseMaps) {
			// update the stats/bonuses in case the player has switched weapons/armour etc
			attackable.setStatsAndBonuses();
			other.setStatsAndBonuses();

			int hit = attackable.hit();
			int attackBonus = attackable.getStats().get(Stats.ACCURACY) + attackable.getBoosts().get(Stats.ACCURACY) + attackable.getBonuses().get(Stats.ACCURACY);
			int defenceBonus = other.getStats().get(Stats.DEFENCE) + other.getBoosts().get(Stats.DEFENCE) + other.getBonuses().get(Stats.DEFENCE);
			
			int baseBlockChance = 25;
			int blockChance = baseBlockChance + Math.max(-baseBlockChance, (defenceBonus - attackBonus) / 2);
			blockChance = other.postBlockChanceModifications(blockChance);
			
			if (RandomUtil.getRandom(0, 100) < blockChance)
				hit = 0;
			
			other.onHit(hit, DamageTypes.STANDARD, responseMaps);
			
			// smite code... pretty awkward to have it here...
			if (attackable instanceof Player && other instanceof Player) {
				Player a = (Player)attackable;
				Player o = (Player)other;
				int smiteAmount = 0;
				if (a.prayerIsActive(Prayers.SMITE))
					smiteAmount = hit / 4;
				else if (a.prayerIsActive(Prayers.ULTRASMITE))
					smiteAmount = hit / 3;
				
				if (smiteAmount > 0) {
					o.setPrayerPoints(o.getPrayerPoints() - smiteAmount, responseMaps);
				}
			}
			
			return other.getCurrentHp() == 0;
		}
	}
	
	public static void process(ResponseMaps responseMaps) {
		// we want to copy all the fights into a different container, as if a fight ends during processing
		// then the fight will be removed from the main fighting list.
		List<Fight> fightsCopy = fights.stream().collect(Collectors.toList());
		for (Fight fight : fightsCopy) {
			fight.process(responseMaps);
		}
	}
	
	private static Fight getFightWithFighter(Attackable fighter) {
		for (Fight fight : fights) {
			if (fight.getFighter1() == fighter || fight.getFighter2() == fighter)
				return fight;
		}
		return null;
	}
	
	public static Fight getFightByPlayerId(int playerId) {
		for (Fight fight : fights) {
			if (((Player)fight.getFighter1()).getId() == playerId)
				return fight;
			
			if (fight.getFighter2() instanceof Player && ((Player)fight.getFighter2()).getId() == playerId)
				return fight;
		}
		
		return null;
	}
	
	public static boolean fightWithFighterExists(Attackable fighter) {
		return getFightWithFighter(fighter) != null;
	}
	
	public static boolean fightWithFighterIsBattleLocked(Attackable fighter) {
		Fight fight = getFightWithFighter(fighter);
		if (fight == null)
			return false;
		return fight.isBattleLocked();
	}
	
	public static void addFight(Attackable fighter1, Attackable fighter2, boolean fighter1initiated) {
		fights.add(new Fight(fighter1, fighter2, fighter1initiated));
	}
	
	public static void cancelFight(Attackable participant, ResponseMaps responseMaps) {
		for (Fight fight : fights) {
			if (fight.getFighter1() == participant || fight.getFighter2() == participant) {
				// clear the target from the player.
				// if we're cancelling a fight with an npc, the npc should keep the target so it can chase.
				fight.getFighter1().setTarget(null);
				
				// fighter1 is always a player, fighter2 can be a player or npc
				if (responseMaps != null) {// todo figure out endpoint onclose cancel fight
					if (fight.getFighter2() instanceof NPC) {
						PvmEndResponse resp = new PvmEndResponse();
						resp.setPlayerId(((Player)fight.getFighter1()).getId());
						resp.setMonsterId(((NPC)fight.getFighter2()).getInstanceId());
						resp.setPlayerTileId(fight.getFighter1().getTileId());
						resp.setMonsterTileId(fight.getFighter2().getTileId());
						responseMaps.addLocalResponse(participant.getFloor(), participant.getTileId(), resp);
					} else {
						fight.getFighter2().setTarget(null);
						PvpEndResponse resp = new PvpEndResponse();
						resp.setPlayer1Id(((Player)fight.getFighter1()).getId());
						resp.setPlayer2Id(((Player)fight.getFighter2()).getId());
						resp.setPlayer1TileId(fight.getFighter1().getTileId());
						resp.setPlayer2TileId(fight.getFighter2().getTileId());
						responseMaps.addLocalResponse(participant.getFloor(), participant.getTileId(), resp);
					}
				}
				fights.remove(fight);
				return;
			}
		}
	}
	
	public static boolean fightingWith(Attackable fighter1, Attackable fighter2) {
		Fight fight = getFightWithFighter(fighter1);
		if (fight == null)
			return false;
		
		if (fight.getFighter1() == fighter2 || fight.getFighter2() == fighter2)
			return true;
		return false;
	}
}
