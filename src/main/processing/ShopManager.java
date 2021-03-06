package main.processing;

import java.util.ArrayList;
import lombok.Getter;
import main.database.ShopDao;
import main.database.ShopDto;
import main.responses.ResponseMaps;
import main.types.ShopTypes;

public class ShopManager {
	private static int UPDATE_TIMER = 100;
	private static int updateTime = UPDATE_TIMER;
	@Getter private static ArrayList<Store> shops = new ArrayList<>();
	
	public static void setupShops() {
		ArrayList<ShopDto> shopDtos = ShopDao.getShopsAndItems();
		
		for (ShopDto dto : shopDtos) {
			ShopTypes type = ShopTypes.withValue(dto.getShopType());
			if (type == null)
				continue;
			
			switch (type) {
			case GENERAL: {//general
				shops.add(new GeneralStore(dto));
				break;
			}
			
			case SPECIALTY: {//specialty
				shops.add(new SpecialtyStore(dto));
				break;
			}
			
			default:
				break;
			}
		}
	}
	
	public static void process(ResponseMaps responseMaps) {
//		if (--updateTime > 0)
//			return;
//		
//		updateTime = UPDATE_TIMER;
		
		for (Store shop : shops) {
			shop.process(responseMaps);
		}
	}
	
	public static void addItem(int shopId, int itemId, int count) {
		for (Store shop : shops) {
			if (shop.getShopId() == shopId) {
				shop.addItem(itemId, count);
			}
		}
	}
	
	public static Store getShopByOwnerId(int ownerId) {
		for (Store shop : shops) {
			if (shop.getOwnerId() == ownerId)
				return shop;
		}
		return null;
	}
	
	public static Store getShopByShopId(int shopId) {
		for (Store shop : shops) {
			if (shop.getShopId() == shopId)
				return shop;
		}
		return null;
	}
}
