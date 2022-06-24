package br.com.zenix.oitc.game.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.oitc.OITC;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class MinigameCommand extends Command {

	private Manager manager;
	public boolean enabled = true;
	public static final String ERROR = "§c§lERROR §f";
	public static final String NO_PERMISSION = "§c§lPERMISSAO §f";
	public static final String OFFLINE = "§c§lOFFLINE §f";

	public MinigameCommand(String name) {
		super(name);
	}

	public MinigameCommand(String name, String description) {
		super(name, description, "", new ArrayList<String>());
	}

	public MinigameCommand(String name, String description, List<String> aliases) {
		super(name, description, "", aliases);
	}

	public abstract boolean execute(CommandSender commandSender, String label, String[] args);

	public Manager getManager() {
		this.manager = OITC.getManager();
		return manager;
	}

	public boolean hasPermission(CommandSender sender, String perm) {
		return sender.hasPermission("oitc.cmd." + perm);
	}

	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static String getError() {
		return ERROR;
	}

	public static String getOffline() {
		return OFFLINE;
	}

	public static String getNoPermission() {
		return NO_PERMISSION;
	}

	public void sendNumericMessage(CommandSender commandSender) {
		commandSender.sendMessage(getError() + "Você inseriu uma informação errada. O argumento é um §c§lNUMERAL.");
	}

	public void sendPermissionMessage(CommandSender commandSender) {
		commandSender.sendMessage(getNoPermission() + "Você não tem §c§lPERMISSAO§f para executar esse comando!");
	}

	public void sendExecutorMessage(CommandSender commandSender) {
		commandSender.sendMessage("ERRO: Somente players podem usar esse comando.");
	}

	public void sendArgumentMessage(CommandSender commandSender, String command, String args) {
		Random random = new Random();
		int randomColor = random.nextInt(9);

		commandSender.sendMessage(
				"§" + randomColor + command.toUpperCase() + ChatColor.RESET + " Use correto: §" + randomColor + args);

	}
	
	public void sendWarning(String warning) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("oitc.alerts.admin")) {
				player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "[" + warning + "]");
			}
		}
	}
}