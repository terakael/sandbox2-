package main.database;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroundTextureDto {
	private int id;
	private int roomId;
	private int spriteMapId;
	private int x;
	private int y;
	private HashSet<Integer> instances;
}
