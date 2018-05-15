package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {
	private ItemDao() {};
	
	public static ItemDto getItemById(int id) {
		final String query = "select id, name, description, sprite_frame_id, context_options from items where id=?";
		
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query);
		) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return new ItemDto(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("sprite_frame_id"), rs.getInt("context_options"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static List<ItemDto> getAllItems() {
		final String query = "select id, name, description, sprite_frame_id, context_options from items";
		List<ItemDto> items = new ArrayList<>();
		
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query);
		) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					items.add(new ItemDto(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("sprite_frame_id"), rs.getInt("context_options")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return items;
	}
}
