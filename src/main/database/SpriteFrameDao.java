package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteFrameDao {
	private static Map<Integer, SpriteFrameDto> allSpriteFrames;
	
	private SpriteFrameDao() {};
	
	public static void setupCaches() {
		cacheAllSpriteFrames();
	}
	
	public static List<SpriteFrameDto> getAllSpriteFrames() {
		return new ArrayList<>(allSpriteFrames.values());
	}
	
	private static void cacheAllSpriteFrames() {
		final String query = "select id, sprite_map_id, x, y, w, h, anchor_x, anchor_y, scale_x, scale_y, margin, frame_count, framerate, animation_type_id from sprite_frames";
		allSpriteFrames = new HashMap<>();
		
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
		) {
			while (rs.next()) {
				allSpriteFrames.put(rs.getInt("id"), new SpriteFrameDto(
						rs.getInt("id"),
						rs.getInt("sprite_map_id"),
						rs.getInt("x"),
						rs.getInt("y"),
						rs.getInt("w"),
						rs.getInt("h"),
						rs.getFloat("anchor_x"),
						rs.getFloat("anchor_y"),
						rs.getFloat("scale_x"),
						rs.getFloat("scale_y"),
						rs.getInt("margin"),
						rs.getInt("frame_count"),
						rs.getInt("framerate"),
						rs.getInt("animation_type_id")
					));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static SpriteFrameDto getSpriteFrameDtoById(int id) {
		if (allSpriteFrames.containsKey(id))
			return allSpriteFrames.get(id);
		return null;
	}
}
