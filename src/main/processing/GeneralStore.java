package main.processing;

import java.util.ArrayList;
import java.util.HashMap;

import main.database.ItemDao;
import main.database.ItemDto;
import main.database.ShopDto;
import main.responses.ResponseMaps;

public class GeneralStore extends Store {
	private ArrayList<ShopDto> playerItems = new ArrayList<>();
	
	public GeneralStore(int id, int ownerId, ArrayList<ShopDto> baseItems) {
		super(id, ownerId, baseItems);
		this.shopId = id;
		this.ownerId = ownerId;
		this.baseItems = baseItems;
	}
	
	@Override
	public void process(ResponseMaps responseMaps) {
		super.process(responseMaps);
		
		// iterate backwards to handle potential removal of items when stock hits 0
		for (int i = playerItems.size() - 1; i >= 0; --i) {
			ShopDto item = playerItems.get(i);
			if (item.getCurrentStock() < item.getMaxStock())
				addItem(item.getItemId(), 1);
			else if (item.getCurrentStock() > item.getMaxStock())
				decreaseItemStock(item.getItemId(), 1);
		}
	}
	
	@Override
	public void addItem(int itemId, int count) {
		ItemDto item = ItemDao.getItem(itemId);
		if (item == null)
			return;
		
		dirty = true;
		
		// check base items first
		for (ShopDto stock : baseItems) {
			if (stock.getItemId() == itemId) {
				stock.setCurrentStock(stock.getCurrentStock() + count);
				return;
			}
		}
		
		// check if it exists in player items
		for (ShopDto stock: playerItems) {
			if (stock.getItemId() == itemId) {
				stock.setCurrentStock(stock.getCurrentStock() + count);
				return;
			}
		}
		
		// new item, add new playerItem entry
		playerItems.add(new ShopDto(itemId, count, 0, item.getPrice()));
	}
	
	@Override
	public HashMap<Integer, ShopDto> getStock() {
		HashMap<Integer, ShopDto> stock = new HashMap<>();
		int slot = 0;
		for (int i = 0; i < baseItems.size(); ++i)
			stock.put(slot++, baseItems.get(i));
		
		for (int i = 0; i < playerItems.size(); ++i)
			stock.put(slot++, playerItems.get(i));
		
		return stock;
	}
	
	@Override
	public ShopDto getStockByItemId(int itemId) {
		ShopDto item = super.getStockByItemId(itemId);
		if (item == null) {
			for (ShopDto stock : playerItems) {
				if (stock.getItemId() == itemId)
					return stock;
			}
		}
		
		return null;
	}
	
	@Override
	public void decreaseItemStock(int itemId, int amount) {
		ShopDto item = getStockByItemId(itemId);
		item.setCurrentStock(item.getCurrentStock() - amount);
		
		if (item.getCurrentStock() <= 0) {
			// leave the baseItems with 0 stock and don't remove them, but remove the player stock
			if (playerItems.contains(item))
				playerItems.remove(item);
		}
		
		dirty = true;
	}
	
	@Override
	public boolean isFull() {
		return baseItems.size() + playerItems.size() >= maxStock;
	}
	
	@Override
	public boolean buysItem(int itemId) {
		return true;
	}
}