package br.com.zenix.oitc.game.handler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.bukkit.commands.base.MessagesType;
import br.com.zenix.core.bukkit.player.events.ServerTimeEvent;
import br.com.zenix.core.master.utilitaries.Utils;
import br.com.zenix.core.proxy.server.ServerStatus;
import br.com.zenix.oitc.file.FileManager;
import br.com.zenix.oitc.manager.Management;
import br.com.zenix.oitc.manager.Manager;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Timer extends Management implements Listener {

	protected Boolean star;

	private boolean start, startg;

	public Timer(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		this.start = false;

		FileManager.getWorld().setSpawnLocation(0, FileManager.getWorld().getHighestBlockYAt(0, 0) + 15, 0);
		return true;
	}
	
	@EventHandler
	public void onTimer(ServerTimeEvent event) {
		if (getManager().getGameManager().isPreGame()) {

			Bukkit.getWorlds().get(0).setTime(0);
			
			getManager().getCoreManager().setServerStatus(ServerStatus.PREGAME, "Aguardando players.");

			getManager().getCoreManager().setServerStatus(ServerStatus.PREGAME,
					"O jogo começa em " + getManager().getUtils().formatTime(getTime()));

			for (Player players : Bukkit.getOnlinePlayers()) {
				MessagesType.sendActionBarMessage(players, "§eO jogo começa em §c" + getManager().getUtils().formatTime(getTime()));
			}
			
			if (Bukkit.getOnlinePlayers().size() <= 8 && this.start == false) {
				return;
			} else {
				this.start = true;
			}

			getManager().getGameManager().setGameTime(getTime() - 1);

			if (getTime() > 60 && Bukkit.getOnlinePlayers().size() >= 3) {
				if (startg == false) {
					startg = true;

					getManager().getGameManager().setGameTime(15);
					Bukkit.broadcastMessage("§3§lJOGO §fO tempo foi reduzido pois o jogo irá iniciar!");
				}
			}

			if (60 <= getBorderTime())
				getManager().getGameManager().setBorderTime(getBorderTime() - 5);

			if ((getTime() % 30 == 0 || getTime() % 60 == 0) && getTime() >= 30) {
				Bukkit.broadcastMessage(
						"§3§lJOGO §fO jogo iniciará em §c§l" + getManager().getUtils().formatTime(getTime()));
				playSound(Sound.CLICK);
			}

			if (getTime() % 5 == 0 && getTime() >= 10 && getTime() <= 20) {
				Bukkit.broadcastMessage(
						"§3§lJOGO §fO jogo iniciará em §c§l" + getManager().getUtils().formatTime(getTime()));
				playSound(Sound.CLICK);
			}

			if (getTime() % 1 == 0 && getTime() <= 5 && getTime() != 0) {
				Bukkit.broadcastMessage(
						"§3§lJOGO §fO jogo iniciará em §c§l" + getManager().getUtils().formatTime(getTime()));
				playSound(Sound.NOTE_PLING);
			}

			if (getTime() <= 0) {
				for (Player data : Bukkit.getOnlinePlayers()) {
					if (!data.hasPermission("oitc.cmd.vanish")) {
						data.closeInventory();
					}
					getManager().getGamerManager().teleportSpawn(data);
				}
				
				startGame();

				for (Gamer gamer : getManager().getGamerManager().getGamers().values())
					gamer.getAccount().setScoreboardHandler(null);
			}

		} else if (getManager().getGameManager().isGame()) {

			getManager().getCoreManager().setServerStatus(ServerStatus.GAME,
					"O jogo irá acabar em " + getManager().getUtils().formatTime(getTime()));
			
			for (Player players : Bukkit.getOnlinePlayers()) {
				MessagesType.sendActionBarMessage(players, "§eO jogo acaba em §c" + getManager().getUtils().formatTime(getTime()));
			}

			getManager().getGameManager().setGameTime(getTime() - 1);
			getManager().getGamerManager().checkWinner();
		}
	}

	public void startGame() {
		getManager().getGameManager().setGameStage(GameState.GAME);
		getManager().getGameManager().setGameTime(300);

		Bukkit.broadcastMessage("§cO jogo iniciou!");
		Bukkit.broadcastMessage("§cMate o quanto você conseguir!");
		Bukkit.broadcastMessage("§bQue a sorte esteja ao seu favor");

		playSound(Sound.ENDERDRAGON_GROWL);

		for (Player data : Bukkit.getOnlinePlayers()) {
			MessagesType.sendTitleMessage(data, "§6§lO jogo iniciou", "§fMate o quanto conseguir!");
		}

		getManager().getCoreManager().getMatchmakingManager().getActualMatch().setStart(Utils.unixTimestamp());

		for (Gamer gamer : getManager().getGamerManager().getAliveGamers()) {

			if (!getManager().getAdminManager().isAdmin(gamer.getPlayer())) {
				startGamer(gamer);
			} else {
				gamer.setGameKills(0);
				getManager().getGamerManager().setDied(gamer);
				getManager().getGamerManager().checkWinner();
			}

			gamer.setGameKills(0);
			gamer.getAccount().setScoreboardHandler(null);
		}
	}

	public void startGamer(Gamer gamer) {
		gamer.getPlayer().getInventory().clear();
		gamer.getPlayer().getActivePotionEffects().clear();
		gamer.getPlayer().getInventory().setArmorContents(null);
		gamer.getPlayer().setFireTicks(0);
		gamer.getPlayer().setFoodLevel(20);
		gamer.getPlayer().setFlying(false);
		gamer.getPlayer().setAllowFlight(false);
		gamer.getPlayer().setSaturation(3.2F);
		gamer.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
		gamer.getPlayer().getInventory().addItem(new ItemStack(Material.BOW));
		gamer.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
		gamer.setGameKills(0);
		gamer.setItemsGive(true);
		gamer.getPlayer().updateInventory();
	}

	public int getBorderSize() {
		return 500;
	}

	public Integer getTime() {
		return getManager().getGameManager().getGameTime();
	}

	public Integer getBorderTime() {
		return getManager().getGameManager().getBorderTime();
	}

	public void playSound(Sound sound) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), sound, 5.0F, 5.0F);
		}
	}
}
