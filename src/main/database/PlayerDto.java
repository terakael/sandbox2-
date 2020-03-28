package main.database;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.types.PlayerPartType;

@Data
@AllArgsConstructor
public class PlayerDto {
	private int id;
	private String name;
	private transient String password;
	private int tileId;
	private int floor;
	private int currentHp;
	private int maxHp;
	private int combatLevel;
	private int attackStyleId;
	private Map<PlayerPartType, AnimationDto> baseAnimations;
	private Map<PlayerPartType, AnimationDto> equipAnimations;
}
