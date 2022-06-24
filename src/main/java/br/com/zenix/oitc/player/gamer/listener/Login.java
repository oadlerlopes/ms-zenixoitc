package br.com.zenix.oitc.player.gamer.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Login extends MinigameListener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void login(PlayerLoginEvent event) {
		if (event.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player.getUniqueId());
		getManager().getGamerManager().getLogger().log("The player with uuid " + player.getUniqueId() + " and nickname "
				+ player.getName() + " logged into the server, starting to load her status.");

		if (gamer != null) {
			getManager().getGamerManager().getLogger().log("The player with uuid " + player.getUniqueId() + "("
					+ player.getName() + ") its already loaded, skipping a few processes.");
		} else {
			gamer = new Gamer(getManager().getCoreManager().getAccountManager().getAccount(player));
			getManager().getGamerManager().addGamer(gamer);
			getManager().getGamerManager().getLogger().log("The player with uuid " + player.getUniqueId() + "("
					+ player.getName() + ") was loaded correctly.");
		}

		gamer.updatePlayer(event.getPlayer());
	}

}
