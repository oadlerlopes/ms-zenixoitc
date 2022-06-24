package br.com.zenix.oitc.game.handler.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import br.com.zenix.core.bukkit.commands.base.MessagesType;
import br.com.zenix.core.bukkit.player.events.PlayerAdminEvent;
import br.com.zenix.core.bukkit.player.events.PlayerChatCoreEvent;
import br.com.zenix.core.bukkit.player.events.PlayerTellCoreEvent;
import br.com.zenix.core.bukkit.player.events.ServerTimeEvent;
import br.com.zenix.core.bukkit.player.item.ItemBuilder;
import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.game.handler.GameState;
import br.com.zenix.oitc.game.handler.LoadItems;
import br.com.zenix.oitc.player.gamer.Gamer;
import br.com.zenix.oitc.player.gamer.GamerMode;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class GameListener extends MinigameListener {

	@EventHandler
	public void onTell(PlayerTellCoreEvent event) {
		Gamer tell = getManager().getGamerManager().getGamer(event.getTarget());

		if (!tell.isAlive())
			event.setCancelled(true);
	}

	@EventHandler
	public void onChat(PlayerChatCoreEvent event) {
		if (event.isCancelled())
			return;

		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());

		if (gamer.isSpectating()) {
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (!gamer.isAlive() && !event.getPlayer().hasPermission("oitc.chat")) {
					event.setCancelled(true);
					if (!getManager().getGamerManager().getGamer(players).isAlive()) {
						players.sendMessage("§7[SPECTATE] "
								+ getManager().getCoreManager().getTagManager().getDisplayName(event.getPlayer())
								+ "§f: " + event.getMessage());
					}
				}
			}
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			player.setSaturation(3.0F);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onTimePre(ServerTimeEvent event) {
		for (Gamer gamers : getManager().getGamerManager().getGamers().values()) {

			if (gamers.getTimer() <= 0) {
				gamers.setAllow(true);
			} else {
				gamers.setTimer(gamers.getTimer() - 1);
				gamers.setAllow(false);
			}

			if (getManager().getGameManager().getGameStage() == GameState.PREGAME) {
				if (gamers.isAlive()) {
					for (int i = 0; i < 9; i++) {
						gamers.getPlayer().getInventory().setItem(i,
								new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE)
										.setDurability(new Random().nextInt(14)).getStack());
					}
				}
			}
		}

		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Arrow) {
					Arrow arrow = (Arrow) entity;
					arrow.remove();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {

		event.setJoinMessage(null);
		Player player = event.getPlayer();

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		if (getManager().getGameManager().isPreGame()) {
			event.getPlayer().getInventory().clear();

			getManager().getGamerManager().givePreGameItems(player);
			player.setGameMode(GameMode.SURVIVAL);

			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.getInventory().setArmorContents(null);
			player.setFireTicks(0);
			player.setFoodLevel(20);
			player.setFlying(false);
			player.setAllowFlight(false);
			player.setSaturation(3.2F);
			player.getActivePotionEffects().clear();

			player.sendMessage(" ");
			player.sendMessage("§6§lOITC");
			player.sendMessage(" ");
			player.sendMessage("§fMate o quanto você conseguir com o seu arco!");
			player.sendMessage(" ");
			player.sendMessage("§9§lTENHA UM BOM JOGO!");

			getManager().getGamerManager().teleportSpawn(player);
			getManager().getGamerManager().getGamer(player).setMode(GamerMode.ALIVE);

			Bukkit.broadcastMessage("§e" + player.getName() + " se juntou ao jogo. §b("
					+ (getManager().getGamerManager().getAliveGamers().size() + ((Bukkit.getOnlinePlayers().size() - getManager().getGamerManager().getAliveGamers().size()))) + "/" + Bukkit.getMaxPlayers() + ")");

			MessagesType.sendTitleMessage(player, "§e§lOITC", "§7Mate o quanto conseguir");

		} else {
			gamer.setOnline(true);

			if (getManager().getGamerManager().getAFKGamers().contains(gamer)) {
				return;
			}

			if (!gamer.isAlive()) {
				if (gamer.getMode() == GamerMode.LOADING) {
					getManager().getGamerManager().setSpectator(gamer, true);

					getManager().getGamerManager().teleportSpawn(player);
				}
			}

		}
		getManager().getGamerManager().hideSpecs(player);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player) && (event.getDamager() instanceof Arrow)) {
			Arrow arrow = (Arrow) event.getDamager();
			
			if (arrow.getShooter() instanceof Player) {
				Player attacker = (Player) arrow.getShooter();
				Player player = (Player) event.getEntity();

				Gamer gamer1 = getManager().getGamerManager().getGamer(attacker);
				Gamer gamer2 = getManager().getGamerManager().getGamer(player);

				if (!gamer2.isSpecs() && !gamer1.isSpecs()) {
					if (!player.getName().equalsIgnoreCase(attacker.getName())) {
						event.setDamage(100.0D);
					} else {
						event.setCancelled(true);
					}
				}
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player);

		if (getManager().getGamerManager().isEndgame()) {
			event.disallow(Result.KICK_OTHER,
					"\n§6§lJOGO §fO jogo acabou!\n§fO servidor está reiniciando!!\n§6www.zenix.cc");
		}
		if (!getManager().getGamerManager().getAFKGamers().contains(gamer)) {

			if (getManager().getGameManager().isGame())
				if (!player.hasPermission("oitc.addon.spec"))
					event.disallow(Result.KICK_OTHER,
							"\n§6§lJOGO §fO jogo já iniciou!\n§fAdquira §e§lVIP§f para poder espectar!\n§6www.zenix.cc");
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());

		if (getManager().getGameManager().isGame()) {
			if (gamer.isAlive()) {
				getManager().getGamerManager().setDied(gamer);
				getManager().getGamerManager().setLeave(gamer);
				getManager().getGamerManager().checkWinner();

				Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + " desistiu do jogo. §b("
						+ (getManager().getGamerManager().getAliveGamers().size() + ((Bukkit.getOnlinePlayers().size() - getManager().getGamerManager().getAliveGamers().size()))) + "/" + Bukkit.getMaxPlayers() + ")");
			}
		} else if (!getManager().getGameManager().isPreGame()) {
			if (gamer.isAlive()) {
				getManager().getGamerManager().getAFKGamers().remove(gamer);
				getManager().getGamerManager().setDied(gamer);
				getManager().getGamerManager().setLeave(gamer);
				getManager().getGamerManager().checkWinner();
				getManager().getGamerManager().getAFKGamers().remove(gamer);

				Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + " desistiu do jogo. §b("
						+ (getManager().getGamerManager().getAliveGamers().size() + ((Bukkit.getOnlinePlayers().size() - getManager().getGamerManager().getAliveGamers().size()))) + "/" + Bukkit.getMaxPlayers() + ")");
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if (event.getLeaveMessage().toLowerCase().contains("you logged in from another location")) {
			event.setCancelled(true);
		} else if (event.getLeaveMessage().toLowerCase().contains("flying is not enabled on this server")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerAdminEvent(PlayerAdminEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player);
		if (getManager().getGameManager().isPreGame()) {
			if (!event.isJoin()) {
				player.updateInventory();

				gamer.setMode(GamerMode.ALIVE);
				gamer.setItemsGive(false);
				getManager().getScoreListener().createScoreboard(player);
			} else {
				player.updateInventory();

				getManager().getGamerManager().setDied(gamer);
			}
		} else {
			if (gamer.isAlive()) {
				getManager().getGamerManager().setDied(gamer);
				getManager().getGamerManager().checkWinner();

				Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + " desistiu do jogo. §b("
						+ getManager().getGamerManager().getAliveGamers().size() + "/" + Bukkit.getMaxPlayers() + ")");
			}

			LoadItems.SPEC.build(player);

			player.updateInventory();

		}
	}

	@EventHandler
	public void onBrakBlock(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()
				&& !event.getPlayer().hasPermission("oitc.staff")) {
			event.setCancelled(true);
			return;
		}

		event.setCancelled(true);
	}

}
