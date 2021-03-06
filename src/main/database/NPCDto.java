package main.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NPCDto {
	private int id;
	private String name;
	private int upId;
	private int downId;
	private int leftId;
	private int rightId;
	private int attackId;
	private float scaleX;
	private float scaleY;
	private int tileId;
	private int hp;
	private int cmb;
	private int leftclickOption;
	private int otherOptions;
	private int floor;
	
	// below are data that doesn't get sent to client as the client doesn't need to see these
	private transient int acc;
	private transient int str;
	private transient int def;
	private transient int pray;
	private transient int magic;
	private transient int accBonus;
	private transient int strBonus;
	private transient int defBonus;
	private transient int prayBonus;
	private transient int attackSpeed;
	private transient int roamRadius;
	private transient int attributes;
	private transient int respawnTicks;
}
