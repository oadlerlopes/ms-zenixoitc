package br.com.zenix.oitc.manager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.bukkit.Core;
import br.com.zenix.core.bukkit.manager.CoreManager;
import br.com.zenix.core.bukkit.server.type.ServerType;
import br.com.zenix.core.master.data.management.DataManager;
import br.com.zenix.core.master.logger.FormattedLogger;
import br.com.zenix.core.master.utilitaries.Utils;
import br.com.zenix.oitc.OITC;
import br.com.zenix.oitc.file.FileManager;
import br.com.zenix.oitc.game.handler.GameManager;
import br.com.zenix.oitc.loaders.ClassLoader;
import br.com.zenix.oitc.player.admin.AdminManager;
import br.com.zenix.oitc.player.admin.Vanish;
import br.com.zenix.oitc.player.gamer.GamerManager;
import br.com.zenix.oitc.player.scoreboard.PlayerScoreboard;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Manager {

	private final CoreManager coreManager;

	private OITC plugin;
	private Utils utils;

	private FileManager fileManager;

	private GamerManager gamerManager;
	private GameManager gameManager;

	private PlayerScoreboard playerScoreboard;

	private AdminManager adminManager;

	private ClassLoader classLoader;

	private Random random;
	private Vanish vanish;

	private int time;

	public Manager(Core core) {
		this.coreManager = Core.getCoreManager();

		plugin = OITC.getPlugin(OITC.class);
		plugin.saveDefaultConfig();

		getLogger().log(
				"Starting the plugin " + plugin.getName() + " version " + plugin.getDescription().getVersion() + "...");
		if (!plugin.TEST_SERVER()) {
			getLogger().log("Starting to loading all the chunks of the world.");

			getLogger().log("The chunks that will be used, was loaded.");
		}
		
		getCoreManager().setServerType(ServerType.OITC);

		getLogger().log("Making connection with plugin " + coreManager.getPlugin().getName() + " version "
				+ coreManager.getPlugin().getDescription().getVersion() + ".");

		utils = coreManager.getUtils();

		random = new Random();

		fileManager = new FileManager(this);
		if (!fileManager.correctlyStart()) {
			return;
		}

		playerScoreboard = new PlayerScoreboard(this);
		if (!playerScoreboard.correctlyStart()) {
			return;
		}

		gamerManager = new GamerManager(this);
		if (!gamerManager.correctlyStart()) {
			return;
		}

		gameManager = new GameManager(this);
		if (!gameManager.correctlyStart()) {
			return;
		}

		adminManager = new AdminManager(this);
		if (!adminManager.correctlyStart()) {
			return;
		}

		vanish = new Vanish(this);

		classLoader = new ClassLoader(this);
		if (!classLoader.correctlyStart()) {
			return;
		}

		getPlugin().getServer().setWhitelist(false);

		Random random = new Random();
		int rnd = random.nextInt(600);

		time = 3600 + rnd;

		new BukkitRunnable() {
			public void run() {
				if (time <= 0) {
					if (getGameManager().isPreGame()) {
						if (Bukkit.getOnlinePlayers().size() == 0) {

							Bukkit.shutdown();
							cancel();
							return;
						}
					}
				}

				time--;
			}
		}.runTaskTimer(getPlugin(), 0L, 20L);

		getLogger().log("The plugin " + plugin.getName() + " version " + plugin.getDescription().getVersion()
				+ " was started correcly.");
	}

	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, getPlugin());
	}

	public Vanish getVanish() {
		return vanish;
	}

	public FormattedLogger getLogger() {
		return getCoreManager().getLogger();
	}

	public ServerType getServerType() {
		return coreManager.getServerType();
	}

	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	public AdminManager getAdminManager() {
		return adminManager;
	}

	public OITC getPlugin() {
		return plugin;
	}

	public Utils getUtils() {
		return utils;
	}

	public PlayerScoreboard getScoreListener() {
		return playerScoreboard;
	}

	public CoreManager getCoreManager() {
		return coreManager;
	}

	public DataManager getMySQLManager() {
		return coreManager.getDataManager();
	}

	public GamerManager getGamerManager() {
		return gamerManager;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public Random getRandom() {
		return random;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public FileManager getFileManager() {
		return fileManager;
	}
}
