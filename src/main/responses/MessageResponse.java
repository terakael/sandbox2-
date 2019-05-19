package main.responses;

import lombok.Setter;
import main.database.PlayerDao;
import main.database.StatsDao;
import main.processing.PathFinder;
import main.processing.Player;
import main.processing.WorldProcessor;
import main.requests.MessageRequest;
import main.requests.Request;

public class MessageResponse extends Response {
	private String name;
	private int id;
	@Setter private String message;
	@Setter private String colour = "yellow";

	public MessageResponse() {
		setAction("message");
	}

	@Override
	public void process(Request req, Player player, ResponseMaps responseMaps) {
		if (!(req instanceof MessageRequest)) {
			setRecoAndResponseText(0, "funny business");
			return;
		}
		
		MessageRequest messageReq = (MessageRequest)req;
		
		String msg = messageReq.getMessage();
		id = messageReq.getId();
		
		if (msg.length() >= 2 && msg.substring(0, 2).equals("::")) {
			//handleDebugCommand(id, msg, client);
			handleDebugCommand(player, msg.substring(2), responseMaps);
			return;
		}
		
		if (msg.length() > 100)
			msg = msg.substring(0, 100);
		
		name = PlayerDao.getNameFromId(id);
		message = msg;
		
		responseMaps.addLocalResponse(player, this);
	}
	
	private void handleDebugCommand(Player player, String msg, ResponseMaps responseMaps) {
		
		String[] msgParts = msg.split(" ");// the :: prefix should already be removed here
		if (msgParts[0].equals("tele")) {
			handleDebugTele(player, msgParts, responseMaps);
			return;
		}
		
		// below are the god-only commands
		if (!player.isGod()) {
			setRecoAndResponseText(0, "You can't do that.  You're not god.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		if (msgParts[0].matches("^att|str|def|hp|agil|acc|mage|herb|mine|smith$")) {
			
			if (msgParts.length >= 2) {
				handleAddExp(player, msgParts, responseMaps);
			} else {
				setRecoAndResponseText(0, "invalid syntax.");
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
		}
	}
	
	private void handleDebugTele(Player player, String[] msgParts, ResponseMaps responseMaps) {
		// syntax: ::tele (destPlayerName|tileId)[ srcPlayerName]
		if (msgParts.length == 1) {
			// invalid syntax
			setRecoAndResponseText(0, "syntax: ::tele (destPlayerName|tileId)[ srcPlayerName]");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		int destTileId = -1;
		int destPlayerId = PlayerDao.getIdFromName(msgParts[1]);
		if (destPlayerId == -1) {
			// no player exists, maybe it's a tileId
			try {
				destTileId = Integer.parseInt(msgParts[1]);
			} catch (NumberFormatException e) {}
		} else {
			// it's a valid player - but are they logged in?
			Player destPlayer = WorldProcessor.getPlayerById(destPlayerId);
			if (destPlayer != null)
				destTileId = destPlayer.getTileId();
		}
		
		if (destTileId < 0 || destTileId > PathFinder.LENGTH * PathFinder.LENGTH) {
			setRecoAndResponseText(0, "invalid teleport destination.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		Player targetPlayer = null;
		if (msgParts.length == 3 && player.isGod()) {
			// there's a srcPlayerName
			int targetPlayerId = PlayerDao.getIdFromName(msgParts[2]);
			if (targetPlayerId == -1) {
				setRecoAndResponseText(0, "could not find player: " + msgParts[2]);
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
			
			targetPlayer = WorldProcessor.getPlayerById(targetPlayerId);			
			if (targetPlayer == null) {
				setRecoAndResponseText(0, msgParts[2] + " is not currently logged in.");
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
		} else {
			targetPlayer = player;
		}
		
		PlayerUpdateResponse playerUpdate = (PlayerUpdateResponse)ResponseFactory.create("player_update");
		playerUpdate.setId(targetPlayer.getDto().getId());
		playerUpdate.setTile(destTileId);
		targetPlayer.setTileId(destTileId);
		responseMaps.addBroadcastResponse(playerUpdate);
	}
	
	private void handleAddExp(Player player, String[] msgParts, ResponseMaps responseMaps) {
		// msgParts[0] = att/str/def etc
		// msgParts[1] = 234 etc
		// msgParts[2] = playerName (optional)
		if (!msgParts[1].matches("-?\\d+")) {
			setRecoAndResponseText(0, "invalid syntax.");
			responseMaps.addClientOnlyResponse(player, this);
			return;
		}
		
		int targetPlayerId = -1;
		if (msgParts.length == 3) {
			targetPlayerId = PlayerDao.getIdFromName(msgParts[2]);
			if (targetPlayerId == -1) {
				setRecoAndResponseText(0, "could not find player: " + msgParts[2]);
				responseMaps.addClientOnlyResponse(player, this);
				return;
			}
		}
		
		if (targetPlayerId == -1)
			targetPlayerId = player.getDto().getId();
		
		int statId = StatsDao.getStatIdByName(msgParts[0]);
		int exp = Integer.parseInt(msgParts[1]);
		if (statId != -1)
			StatsDao.addExpToPlayer(targetPlayerId, statId, exp);
		
		Player targetPlayer = targetPlayerId == player.getDto().getId() ? player : WorldProcessor.getPlayerById(targetPlayerId); 
		
		// inform god that the target player has received the exp
		if (targetPlayer != player) {
			setRecoAndResponseText(0, String.format("%s has been granted %dexp in %s, my lord.", msgParts[2], exp, msgParts[0]));
			responseMaps.addClientOnlyResponse(player, this);
		}
		
		if (targetPlayer == null)
			return;// targetPlayer is valid based on the id check above, but they're not logged in so we can't send them their response
		
		AddExpResponse resp = new AddExpResponse();
		resp.setId(targetPlayerId);
		resp.setStatId(statId);
		resp.setStatShortName(msgParts[0]);
		resp.setExp(exp);
		
		responseMaps.addClientOnlyResponse(targetPlayer, resp);
		
		MessageResponse messageResponse = new MessageResponse();
		messageResponse.setRecoAndResponseText(0, String.format("Your god has granted you %dexp in %s; %s him!", exp, msgParts[0], exp <= 0 ? "fear" : "praise"));
		responseMaps.addClientOnlyResponse(targetPlayer, messageResponse);
	}
}
