package main.types;

import lombok.Getter;

public enum NpcAttributes {
	ATTACKABLE(1),
	AGGRESSIVE(2);
	
	@Getter private int value;
	NpcAttributes(int value) {
		this.value = value;
	}
	
	public static NpcAttributes withValue(final int val) {
		for (NpcAttributes attribute : NpcAttributes.values()) {
			if (attribute.getValue() == val)
				return attribute;
		}
		return null;
	}
}
