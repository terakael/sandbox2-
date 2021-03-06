package main.types;

import lombok.Getter;

public enum StorageTypes {
	INVENTORY(1),
	BANK(2),
	FURNACE(3),
	TRADE(4);
	
	@Getter private int value;
	StorageTypes(int value) {
		this.value = value;
	}
	
	public static StorageTypes withValue(final int val) {
		for (StorageTypes type : StorageTypes.values()) {
			if (type.getValue() == val)
				return type;
		}
		return null;
	}
}
