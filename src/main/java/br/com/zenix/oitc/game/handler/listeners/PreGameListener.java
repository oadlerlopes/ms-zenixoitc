package br.com.zenix.oitc.game.handler.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import br.com.zenix.core.bukkit.player.events.PlayerInventoryOpenEvent;
import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

@SuppressWarnings("deprecation")
public class PreGameListener extends MinigameListener {

	@EventHandler
	private void onServerListPing(ServerListPingEvent event) {
		String time = getManager().getUtils().toTime(getManager().getGameManager().getGameTime());
		if (getManager().getGameManager().isPreGame()) {
			event.setMotd("§cIniciando em " + time + "!\n§eVisite §ewww.zenix.cc");
		} else {
			event.setMotd("§cEm progresso. Tente www.zenix.cc.\n§eVisite §ewww.zenix.cc");
		}
	}

	@EventHandler
	private void onInventaryOpen(PlayerInventoryOpenEvent event) {
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
		if (gamer.isSpectating()) {
			if (!gamer.getPlayer().hasPermission("oitc.staff")) {
				gamer.sendMessage("§6§lINVENTARIO §fVocê não pode §e§lABRIR§f isto agora.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getCreatureType() == CreatureType.GHAST) {
			event.setCancelled(true);
		}
		if (getManager().getGameManager().isPreGame()) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void aoExplodir(ExplosionPrimeEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onChuva(WeatherChangeEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);

	}

	@EventHandler
	public void onBreakBlocks(BlockBreakEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
		else {
			int border = getManager().getGameManager().getTimer().getBorderSize() - 10;
			Block block = event.getBlock();
			Location worldLocation = block.getWorld().getSpawnLocation();
			if ((Math.abs(block.getLocation().getBlockX() + worldLocation.getBlockX()) >= border)
					|| (Math.abs(block.getLocation().getBlockZ() + worldLocation.getBlockZ()) >= border)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlaceBlocks(BlockPlaceEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
		else {
			int border = getManager().getGameManager().getTimer().getBorderSize() - 10;
			Block block = event.getBlock();
			Location worldLocation = block.getWorld().getSpawnLocation();
			if ((Math.abs(block.getLocation().getBlockX() + worldLocation.getBlockX()) >= border)
					|| (Math.abs(block.getLocation().getBlockZ() + worldLocation.getBlockZ()) >= border)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			if (!(event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}

			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getEntity());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
		}
		if (getManager().getGameManager().isGame()) {
			if (!(event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}

			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getEntity());
			if (!gamer.isAlive()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}

			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getDamager());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
		}
		if (getManager().getGameManager().isEnded()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
		}
		if (getManager().getGameManager().isGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isAlive()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
			if (event.getPlayer().getItemInHand().getType().equals(Material.STONE_SWORD)) {
				event.setCancelled(true);
			}
		}
		if (getManager().getGameManager().isGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isAlive()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
}
