package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import lombok.Getter;

public class LadderConnectionDao {
	@Getter private static ArrayList<LadderConnectionDto> ladderConnections;
	
	public static void setupCaches() {
		final String query = "select from_room_id, from_tile_id, to_room_id, to_tile_id from ladder_connections ";
		try (
			Connection connection = DbConnection.get();
			PreparedStatement ps = connection.prepareStatement(query)
		) {
			try (ResultSet rs = ps.executeQuery()) {
				ladderConnections = new ArrayList<>();
				while (rs.next()) {
					ladderConnections.add(new LadderConnectionDto(rs.getInt("from_room_id"), rs.getInt("from_tile_id"), rs.getInt("to_room_id"), rs.getInt("to_tile_id")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}