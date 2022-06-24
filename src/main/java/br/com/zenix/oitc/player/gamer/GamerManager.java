package br.com.zenix.oitc.player.gamer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.ProtocolInjector;

import br.com.zenix.core.bukkit.commands.base.MessagesType;
import br.com.zenix.core.bukkit.player.account.Account;
import br.com.zenix.core.bukkit.player.matchmaking.handler.HandleMatchKillLeaving;
import br.com.zenix.core.master.data.handler.DataHandler;
import br.com.zenix.core.master.data.handler.type.DataType;
import br.com.zenix.oitc.OITC;
import br.com.zenix.oitc.game.handler.GameState;
import br.com.zenix.oitc.game.handler.LoadItems;
import br.com.zenix.oitc.manager.Management;
import br.com.zenix.oitc.manager.Manager;
import br.com.zenix.oitc.utilitaries.SortMapByValue;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.PlayerConnection;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerManager extends Management {

	private static final HashMap<UUID, Gamer> gamers = new HashMap<>();
	private static final List<Gamer> afkGamers = new ArrayList<>();

	private boolean endGame = false;

	public GamerManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public void addGamer(Gamer gamer) {
		gamers.put(gamer.getUUID(), gamer);
	}

	public boolean isEndgame() {
		return endGame;
	}

	public Gamer getGamer(UUID uuid) {
		return gamers.get(uuid);
	}

	public Gamer getGamer(Player player) {
		return gamers.get(player.getUniqueId());
	}

	public HashMap<UUID, Gamer> getGamers() {
		return gamers;
	}

	public List<Gamer> getAFKGamers() {
		return afkGamers;
	}

	public List<Gamer> getAliveGamers() {
		List<Gamer> gamers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers())
			if (getGamer(player).isAlive())
				gamers.add(getGamer(player));
		return gamers;
	}

	public Collection<? extends Player> getAlivePlayers() {
		List<Player> gamers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers())
			if (getGamer(player).isAlive())
				gamers.add(player);
		return gamers;
	}

	public void givePreGameItems(Player player) {
		player.getInventory().clear();

		player.updateInventory();
	}

	public void hideSpecs(Player player) {
		Gamer gamer = getManager().getGamerManager().getGamer(player);

		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (gamer.isSpecs()) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);

			}
		}

		for (Player players : Bukkit.getOnlinePlayers()) {
			if (!gamer.isSpecs()) {
				if (getManager().getGamerManager().getGamer(players).isSpectating()) {
					player.hidePlayer(players);
				}
			}
		}

		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (toHide.isSpecs()) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);
			}

		}
	}

	public void setSpectator(Gamer gamer, boolean set) {
		gamer.setMode(GamerMode.SPECTING);
		gamer.setItemsGive(true);

		for (Player players : Bukkit.getOnlinePlayers()) {
			if (getManager().getGamerManager().getGamer(players).isSpecs()) {
				players.hidePlayer(gamer.getPlayer());
			}
			players.hidePlayer(gamer.getPlayer());
		}

		final Player player = gamer.getPlayer();

		player.setAllowFlight(true);
		player.setFlying(true);

		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (gamer.isSpecs()) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);
			}
		}

		hideSpecs(player);

		new BukkitRunnable() {
			public void run() {
				player.setGameMode(GameMode.CREATIVE);

				player.closeInventory();
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.getInventory().setArmorContents(new ItemStack[4]);
				player.setAllowFlight(true);
				player.setFlying(true);

				if (set == true) {
					LoadItems.SPEC.build(player);
				}

				for (Player players : Bukkit.getOnlinePlayers()) {
					players.hidePlayer(gamer.getPlayer());
				}

				hideSpecs(player);

			}
		}.runTaskLater(getManager().getPlugin(), 2L);
	}

	public void setDied(Gamer gamer) {
		gamer.setMode(GamerMode.DEAD);
		gamer.setItemsGive(true);
	}

	public void setLeave(Gamer gamer) {
		Bukkit.getScheduler().runTaskAsynchronously(getManager().getPlugin(), new HandleMatchKillLeaving(
				getManager().getCoreManager(), gamer.getAccount(), getAliveGamers().size() + 1));
	}

	public void respawnPlayer(Gamer gamer) {
		gamer.getPlayer().setHealth(20.0D);
	}

	public void applyRespawn(Gamer gamer) {
		gamer.getPlayer().setHealth(20.0);
		gamer.getPlayer().setFoodLevel(20);
		gamer.getPlayer().getInventory().clear();
		gamer.getPlayer().getActivePotionEffects().clear();
		gamer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
		gamer.getPlayer().setFireTicks(0);
		gamer.getPlayer().setFoodLevel(20);
		gamer.getPlayer().setFlying(false);
		gamer.getPlayer().setAllowFlight(false);
		gamer.getPlayer().setSaturation(3.2F);
	}

	public void setRespawn(Gamer gamer) {
		gamer.setMode(GamerMode.ALIVE);

		if (gamer.getPlayer().hasPermission("oitc.respawn")) {

			new BukkitRunnable() {
				public void run() {
					applyRespawn(gamer);
					setSpectator(gamer, true);

					new BukkitRunnable() {
						public void run() {
							gamer.setMode(GamerMode.ALIVE);

							for (Player players : Bukkit.getOnlinePlayers()) {
								players.showPlayer(gamer.getPlayer());
							}

							applyRespawn(gamer);

							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.BOW));
							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
							gamer.getPlayer()
									.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 255));
							getManager().getGamerManager().teleportSpawn(gamer.getPlayer());

							gamer.getPlayer().setGameMode(GameMode.SURVIVAL);
						}
					}.runTaskLater(getManager().getPlugin(), 90L);

				}
			}.runTaskLater(getManager().getPlugin(), 5L);
		} else {
			new BukkitRunnable() {

				public void run() {
					applyRespawn(gamer);
					setSpectator(gamer, true);

					new BukkitRunnable() {
						public void run() {
							gamer.setMode(GamerMode.ALIVE);

							for (Player players : Bukkit.getOnlinePlayers()) {
								players.showPlayer(gamer.getPlayer());
							}

							applyRespawn(gamer);
							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.BOW));
							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
							
							gamer.getPlayer()
									.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 255));
							getManager().getGamerManager().teleportSpawn(gamer.getPlayer());

							gamer.getPlayer().setGameMode(GameMode.SURVIVAL);
						}
					}.runTaskLater(getManager().getPlugin(), 150L);

				}

			}.runTaskLater(getManager().getPlugin(), 5L);
		}
	}

	public void updateTab(Player player) {
		if (player == null)
			return;

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		StringBuilder headerBuilder = new StringBuilder();

		int ping = 0;
		ping = ((CraftPlayer) gamer.getAccount().getPlayer()).getHandle().ping;

		String tempo = getManager().getUtils().formatOldTime(getManager().getGameManager().getGameTime());
		headerBuilder.append("\n");
		headerBuilder.append(
				"   §e" + tempo + " §6§l> §f" + getManager().getCoreManager().getServerIP() + " §6§l< §e" + " ");

		headerBuilder.append("\n");
		headerBuilder.append("  §6Kills: §e" + gamer.getGameKills() + " §1- §6Ping: §e" + ping + "  ");
		headerBuilder.append("\n");

		StringBuilder footerBuilder = new StringBuilder();
		footerBuilder.append(" \n ");
		footerBuilder.append("§bNick: §f" + gamer.getAccount().getPlayer().getName() + " §1- §bLiga: §f"
				+ gamer.getAccount().getLeague().getName().toUpperCase() + " §1- §bXP: §f"
				+ gamer.getAccount().getXp());
		footerBuilder.append(" \n ");
		footerBuilder.append("§bMais informações em §fwww.zenix.cc");
		footerBuilder.append(" \n ");

		getManager().getGamerManager().updateTab(player, headerBuilder.toString(), footerBuilder.toString());
	}

	public void updateTab(Player player, String up, String down) {
		if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 46) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new ProtocolInjector.PacketTabHeader(ChatSerializer.a("{'text': '" + up + "'}"),
					ChatSerializer.a("{'text': '" + down + "'}")));
		}
	}

	public void teleportSpawn(Player player) {
		Random random = new Random();
		int sort = random.nextInt(18);
		if (sort == 1) {
			player.teleport(new Location(Bukkit.getWorld("world"), 960, 12, 941));
			return;
		} else if (sort == 2) {
			player.teleport(new Location(Bukkit.getWorld("world"), 933, 12, 966));
			return;
		} else if (sort == 3) {
			player.teleport(new Location(Bukkit.getWorld("world"), 914, 10, 971));
			return;
		} else if (sort == 4) {
			player.teleport(new Location(Bukkit.getWorld("world"), 901, 12, 980));
			return;
		} else if (sort == 5) {
			player.teleport(new Location(Bukkit.getWorld("world"), 922, 13, 990));
			return;
		} else if (sort == 6) {
			player.teleport(new Location(Bukkit.getWorld("world"), 920, 11, 1013));
			return;
		} else if (sort == 7) {
			player.teleport(new Location(Bukkit.getWorld("world"), 955, 13, 1006));
			return;
		} else if (sort == 8) {
			player.teleport(new Location(Bukkit.getWorld("world"), 957, 10, 986));
			return;
		} else if (sort == 9) {
			player.teleport(new Location(Bukkit.getWorld("world"), 929, 13, 997));
			return;
		} else if (sort == 10) {
			player.teleport(new Location(Bukkit.getWorld("world"), 976, 8, 939));
			return;
		} else if (sort == 11) {
			player.teleport(new Location(Bukkit.getWorld("world"), 965, 12, 972));
			return;
		} else if (sort == 12) {
			player.teleport(new Location(Bukkit.getWorld("world"), 963, 12, 990));
			return;
		} else if (sort == 13) {
			player.teleport(new Location(Bukkit.getWorld("world"), 959, 8, 1009));
			return;
		} else if (sort == 14) {
			player.teleport(new Location(Bukkit.getWorld("world"), 959, 8, 1009));
			return;
		} else {
			player.teleport(new Location(Bukkit.getWorld("world"), 929, 13, 997));
		}

	}

	public void updateGamer(Account account) {
		account.update();
	}

	public void updateGamer(Gamer gamer) {
		updateGamer(gamer.getAccount());
	}

	public void giveDamage(LivingEntity reciveDamage, Player giveDamage, double damage, boolean bool) {
		if (reciveDamage == null || reciveDamage.isDead() || giveDamage == null || giveDamage.isDead())
			return;

		reciveDamage.setNoDamageTicks(0);

		if (bool) {
			if (reciveDamage.getHealth() < damage) {
				reciveDamage.setHealth(1.0D);
				giveDamage.setMetadata("custom", new FixedMetadataValue(OITC.getPlugin(OITC.class), null));
				reciveDamage.damage(6.0D, giveDamage);
			} else {
				reciveDamage.damage(damage);
			}
		} else {
			giveDamage.setMetadata("custom", new FixedMetadataValue(OITC.getPlugin(OITC.class), null));
			reciveDamage.damage(damage, giveDamage);
		}
	}

	public void checkWinner() {
		if (getManager().getGameManager().isEnded()) {
			return;
		}
		if (getManager().getGameManager().getTimer().getTime() <= 1) {
			getManager().getGameManager().setEnded(true);

			Gamer gamer = topKiller(0);

			if (gamer.getPlayer().isOnline()) {
				makeWin(gamer.getPlayer(), gamer);
			}

			Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
				int cancel = 15;

				public void run() {
					if (cancel == 0) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.kickPlayer("[dead-transfer-player]");
						}
					}
					cancel--;
				}
			}, 0L, 20L);

			Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
				int cancel = 20;

				public void run() {
					if (cancel == 0) {
						getManager().getPlugin().handleStop();
					}
					cancel--;
				}
			}, 0L, 20L);

		} else if (getAliveGamers().size() == 0) {
			getManager().getPlugin().handleStop();

		}
	}

	private Gamer topKiller(int id) {
		return topStreak().get(id);
	}

	public ArrayList<Gamer> topStreak() {
		HashMap<Gamer, Double> scores = new HashMap<Gamer, Double>();

		for (Gamer gamer : getManager().getGamerManager().getGamers().values()) {
			if (gamer.getPlayer() != null) {
				if (gamer.getPlayer().getName() != null) {
					if (Bukkit.getPlayer(gamer.getPlayer().getName()) != null) {
						if (!scores.containsKey(gamer)) {
							scores.put(gamer, (double) gamer.getGameKills());
						}
					}
				}
			}
		}

		return new ArrayList<Gamer>(new SortMapByValue().sortByComparator(scores, false).keySet());
	}

	public void makeWin(Player player, Gamer gamer) {
		getManager().getGameManager().setGameStage(GameState.WINNING);

		for (Player players : Bukkit.getOnlinePlayers()) {
			Gamer gr = getManager().getGamerManager().getGamer(players);
			gr.setMode(GamerMode.ALIVE);

			players.showPlayer(gr.getPlayer());
			applyRespawn(gamer);

			gr.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 255));

			gr.getPlayer().setGameMode(GameMode.SURVIVAL);
			players.getInventory().clear();
		}

		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		player.setGameMode(GameMode.CREATIVE);
		player.getInventory().setArmorContents(null);
		player.updateInventory();

		Account account = gamer.getAccount();
		DataHandler dataHandler = account.getDataHandler();

		dataHandler.getValue(DataType.GLOBAL_XP).setValue(dataHandler.getValue(DataType.GLOBAL_XP).getValue() + 30);
		dataHandler.getValue(DataType.GLOBAL_COINS)
				.setValue(dataHandler.getValue(DataType.GLOBAL_COINS).getValue() + 50);

		dataHandler.update(DataType.GLOBAL_XP);
		dataHandler.update(DataType.GLOBAL_COINS);

		gamer.update();

		endGame = true;

		startFirework(player, player.getLocation(), getManager().getRandom());

		Bukkit.broadcastMessage(
				"§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-=");
		Bukkit.broadcastMessage("§6" + player.getName() + " ganhou!");
		Bukkit.broadcastMessage("§aMatou §2" + gamer.getGameKills() + "§a players em 5 minutos! Parabéns!");
		Bukkit.broadcastMessage(
				"§7§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-=");

		Bukkit.getWorld("world").setTime(13000);

		MessagesType.sendTitleMessage(gamer.getPlayer(), "§cFim de jogo!", "§fVocê ganhou o jogo! Parabéns!");

		for (Player players : Bukkit.getOnlinePlayers()) {
			if (players != gamer.getPlayer()) {
				MessagesType.sendTitleMessage(players, "§cFim de jogo!",
						"§f" + gamer.getPlayer().getName() + "§7 ganhou o jogo!");
			} else {
				MessagesType.sendTitleMessage(players, "§cFim de jogo!", "§fVocê§7 ganhou o jogo!");
			}
			players.setAllowFlight(true);
			spawnRandomFirework(players.getLocation());
		}
	}

	public void startFirework(final Player player, Location location, Random random) {
		for (int i = 0; i < 5; i++) {
			spawnRandomFirework(location.add(-10 + random.nextInt(20), 0.0D, -10 + random.nextInt(20)));
		}
		new BukkitRunnable() {
			public void run() {
				spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, -10.0D));
				spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, 10.0D));
				spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, -10.0D));
				spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, 10.0D));
				spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, -5.0D));
				spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, 5.0D));
				spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, -5.0D));
				spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, 5.0D));
				spawnRandomFirework(player.getLocation().add(-4.0D, 0.0D, -3.0D));
				spawnRandomFirework(player.getLocation().add(-3.0D, 0.0D, 4.0D));
				spawnRandomFirework(player.getLocation().add(2.0D, 0.0D, -6.0D));
				spawnRandomFirework(player.getLocation().add(1.0D, 0.0D, 9.0D));

			}
		}.runTaskTimer(getManager().getPlugin(), 10L, 30L);
	}

	public void spawnRandomFirework(Location location) {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fwm = firework.getFireworkMeta();

		int rt = getManager().getRandom().nextInt(4) + 1;

		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (rt == 1) {
			type = FireworkEffect.Type.BALL;
		} else if (rt == 2) {
			type = FireworkEffect.Type.BALL_LARGE;
		} else if (rt == 3) {
			type = FireworkEffect.Type.BURST;
		} else if (rt == 4) {
			type = FireworkEffect.Type.STAR;
		}

		FireworkEffect effect = FireworkEffect.builder().flicker(getManager().getRandom().nextBoolean())
				.withColor(Color.WHITE).withColor(Color.ORANGE).withFade(Color.FUCHSIA).with(type)
				.trail(getManager().getRandom().nextBoolean()).build();
		fwm.addEffect(effect);
		fwm.setPower(getManager().getRandom().nextInt(2) + 1);

		firework.setFireworkMeta(fwm);
	}

}
