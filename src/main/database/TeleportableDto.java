package main.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeleportableDto {
	private int itemId;
	private int roomId;
	private int tileId;
}
