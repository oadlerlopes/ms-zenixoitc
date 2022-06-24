package br.com.zenix.oitc.player.gamer.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.bukkit.player.account.Account;
import br.com.zenix.core.bukkit.player.league.player.PlayerLeague;
import br.com.zenix.oitc.game.custom.GamerDeathEvent;
import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerDeath extends MinigameListener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!getManager().getGameManager().isPreGame()) {
			event.setDeathMessage(null);

			Gamer player = getManager().getGamerManager().getGamer(event.getEntity());
			Account playerAccount = player.getAccount();

			event.getDrops().clear();

			GamerDeathEvent eventGamer;
			if (event.getEntity().getKiller() instanceof Player) {
				eventGamer = new GamerDeathEvent(event.getEntity().getKiller().getPlayer(), event.getEntity(),
						event.getEntity().getLocation().clone().add(0, 0.5, 0), event.getDrops());
			} else {
				eventGamer = new GamerDeathEvent(event.getEntity().getKiller(), event.getEntity().getPlayer(),
						event.getEntity().getPlayer().getLocation().clone().add(0, 0.5, 0), event.getDrops());
			}

			Bukkit.getPluginManager().callEvent(eventGamer);

			event.getDrops().clear();

			Gamer playerKiller = null;

			if (!(event.getEntity().getKiller() instanceof Player)) {
				getManager().getGamerManager().hideSpecs(player.getPlayer());
			} else {
				playerKiller = getManager().getGamerManager().getGamer(event.getEntity().getKiller());

				playerKiller.setGameKills(playerKiller.getGameKills() + 1);
				playerKiller.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
				playerKiller.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 255));
				getManager().getGamerManager().hideSpecs(player.getPlayer());
			}

			player.update();

			if (event.getEntity().getKiller() instanceof Player) {
				getManager().getGamerManager().hideSpecs(playerKiller.getPlayer());
				new PlayerLeague(playerKiller.getPlayer(), playerAccount.getPlayer()).prizeLeague();

			}

			if (getManager().getGameManager().getTimer().getTime() > 1) {
				getManager().getGamerManager().respawnPlayer(player);
				getManager().getGamerManager().setRespawn(player);
			} else {
				getManager().getGamerManager().setSpectator(player, false);
			}

			getManager().getGamerManager().checkWinner();
		}
	}
}