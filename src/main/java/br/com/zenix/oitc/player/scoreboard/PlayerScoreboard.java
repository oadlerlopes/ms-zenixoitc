package br.com.zenix.oitc.player.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.bukkit.player.account.Account;
import br.com.zenix.core.bukkit.player.scoreboard.AnimatedString;
import br.com.zenix.core.bukkit.player.scoreboard.ScoreboardConstructor;
import br.com.zenix.oitc.manager.Management;
import br.com.zenix.oitc.manager.Manager;
import br.com.zenix.oitc.player.gamer.Gamer;
import br.com.zenix.oitc.utilitaries.SortMapByValue;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class PlayerScoreboard extends Management {

	private String title = "§6§lZENIX";
	private AnimatedString animatedString;

	public PlayerScoreboard(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		animatedString = new AnimatedString(" OITC ", "§f§l", "§6§l", "§e§l", 3);

		return startScores();
	}

	public boolean startScores() {

		new BukkitRunnable() {
			public void run() {

				if (Bukkit.getOnlinePlayers().isEmpty())
					return;

				title = "§f§l" + animatedString.next();
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					updateScoreboard(player);

					getManager().getGamerManager().updateTab(player);
				}
			}
		}.runTaskTimer(getManager().getPlugin(), 2, 2);
		return true;
	}

	public void createScoreboard(Player player) {
		ScoreboardConstructor scoreboardHandler = new ScoreboardConstructor(player);
		scoreboardHandler.initialize(" §6§l" + getManager().getCoreManager().getServerName() + " ");

		scoreboardHandler.setScore("§b§c", "§2§l", "§f");

		if (getManager().getGameManager().isPreGame()) {
			scoreboardHandler.setScore("§fIniciando em", "§f", ": §f0");
			scoreboardHandler.setScore("§fJogadores v", "§f", "ivos: §f0");
			scoreboardHandler.setScore("§fJogadores m", "§f", "ortos: §f0");
		} else if (getManager().getGameManager().isGame()) {
			scoreboardHandler.setScore("§fTempo restan", "§f", "§fte: §f0");
			scoreboardHandler.setScore("§fJogadores v", "§f", "ivos: §f0");
			scoreboardHandler.setScore("§fJogadores m", "§f", "ortos: §f0");
		}

		scoreboardHandler.setScore("§8§f", "§1§l", "§f");
		if (topKiller(0).length() > 16) {
			scoreboardHandler.setScore("§a1º§f " + topKiller(0).substring(0, 9), "§1§l",
					"" + topKiller(0).substring(9, 0));
		} else {
			scoreboardHandler.setScore("§a1º§f ", "§1§l", "" + topKiller(0));
		}
		if (topKiller(1).length() > 16) {
			scoreboardHandler.setScore("§62º§f " + topKiller(1).substring(0, 9), "§1§l",
					"" + topKiller(1).substring(9, 0));
		} else {
			scoreboardHandler.setScore("§62º§f ", "§1§l", "" + topKiller(1));
		}
		if (topKiller(2).length() > 16) {
			scoreboardHandler.setScore("§c3º§f " + topKiller(2).substring(0, 9), "§1§l",
					"" + topKiller(2).substring(9, 0));
		} else {
			scoreboardHandler.setScore("§c3º§f ", "§1§l", "" + topKiller(2));
		}
		if (topKiller(3).length() > 16) {
			scoreboardHandler.setScore("§44º§f " + topKiller(3).substring(0, 9), "§1§l",
					"" + topKiller(3).substring(9, 0));
		} else {
			scoreboardHandler.setScore("§44º§f ", "§1§l", "" + topKiller(3));
		}
		if (topKiller(4).length() > 16) {
			scoreboardHandler.setScore("§d5º§f " + topKiller(4).substring(0, 9), "§1§l",
					"" + topKiller(4).substring(9, 0));
		} else {
			scoreboardHandler.setScore("§d5º§f ", "§1§l", "" + topKiller(4));
		}
		scoreboardHandler.setScore("§5§f", "§1§l", "§f");
		scoreboardHandler.setScore("§fKills: ", "§f", "§90");
		scoreboardHandler.setScore("§fMapa: ", "§f", "§6Mirage");
		scoreboardHandler.setScore("§d§c", "§2§l", "§f");
		scoreboardHandler.setScore("www.zenix", "§6", "§6.cc");

		getManager().getGamerManager().getGamer(player).getAccount().setScoreboardHandler(scoreboardHandler);
	}

	public void updateScoreboard(Player player) {

		Gamer gamer = getManager().getGamerManager().getGamer(player);
		Account account = gamer.getAccount();

		if (account.getScoreboardHandler() == null) {
			createScoreboard(player);
		}

		ScoreboardConstructor scoreboardHandler = getManager().getGamerManager().getGamer(player).getAccount()
				.getScoreboardHandler();
		scoreboardHandler.setDisplayName(title);

		String time = getManager().getUtils().formatOldTime(getManager().getGameManager().getGameTime());

		if (getManager().getGameManager().isPreGame()) {
			scoreboardHandler.updateScore("§fIniciando em", "§f", "§f: §e" + time);
		} else if (getManager().getGameManager().isGame()) {
			scoreboardHandler.updateScore("§fTempo restan", "§f", "§fte: §e" + time);
		}

		scoreboardHandler.updateScore("§fKills: ", "§f", "§b" + gamer.getGameKills());
		if (topKiller(0).length() > 16) {
			scoreboardHandler.updateScore("§a1º§f " + topKiller(0).substring(0, 9), "§1§l",
					"" + topKiller(0).substring(9, 0));
		} else {
			scoreboardHandler.updateScore("§a1º§f ", "§1§l", "" + topKiller(0));
		}
		if (topKiller(1).length() > 16) {
			scoreboardHandler.updateScore("§62º§f " + topKiller(1).substring(0, 9), "§1§l",
					"" + topKiller(1).substring(9, 0));
		} else {
			scoreboardHandler.updateScore("§62º§f ", "§1§l", "" + topKiller(1));
		}
		if (topKiller(2).length() > 16) {
			scoreboardHandler.updateScore("§c3º§f " + topKiller(2).substring(0, 9), "§1§l",
					"" + topKiller(2).substring(9, 0));
		} else {
			scoreboardHandler.updateScore("§c3º§f ", "§1§l", "" + topKiller(2));
		}
		if (topKiller(3).length() > 16) {
			scoreboardHandler.updateScore("§44º§f " + topKiller(3).substring(0, 9), "§1§l",
					"" + topKiller(3).substring(9, 0));
		} else {
			scoreboardHandler.updateScore("§44º§f ", "§1§l", "" + topKiller(3));
		}
		if (topKiller(4).length() > 16) {
			scoreboardHandler.updateScore("§d5º§f " + topKiller(4).substring(0, 9), "§1§l",
					"" + topKiller(4).substring(9, 0));
		} else {
			scoreboardHandler.updateScore("§d5º§f ", "§1§l", "" + topKiller(4));
		}

		scoreboardHandler.updateScore("§fJogadores v", "§f",
				"§fivos: §e" + getManager().getGamerManager().getAliveGamers().size());

		scoreboardHandler.updateScore("§fJogadores m", "§f", "§fortos: §e"
				+ (Bukkit.getOnlinePlayers().size() - getManager().getGamerManager().getAliveGamers().size()));
	}

	private String topKiller(int id) {
		String name = topStreak().size() > id ? topStreak().get(id).getPlayer().getName() : "NRE";
		String text = name.equals("NRE") ? "Ninguém "
				: name + " " + (topStreak().size() > id ? topStreak().get(id).getGameKills() : 0);
		return text;
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

}
