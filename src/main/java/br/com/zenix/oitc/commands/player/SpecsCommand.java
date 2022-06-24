package br.com.zenix.oitc.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.oitc.game.custom.MinigameCommand;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class SpecsCommand extends MinigameCommand {

	public SpecsCommand() {
		super("specs", "Esconda ou veja os espectadores.");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (args.length != 1) {
			commandSender.sendMessage("§a§lSPECS §fUse: /specs <on/off>");
			return false;
		}

		if (args[0].equalsIgnoreCase("on") && args[0].equalsIgnoreCase("off")) {
			commandSender.sendMessage("§a§lSPECS §fUse: /specs <on/off>");
			return false;
		}

		Player player = (Player) commandSender;
		boolean hidePlayers = args[0].toLowerCase().equals("off") ? true : false;
		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (hidePlayers) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);
			} else {
				player.showPlayer(playerToHide);
			}
		}
		getManager().getGamerManager().getGamer(player).setSpecs(!getManager().getGamerManager().getGamer(player).isSpecs());
		commandSender.sendMessage("§a§lSPECS §fAgora você " + (hidePlayers ? "§c§lescondeu" : "§a§lvê") + "§f os espectadores.");

		return true;
	}
}
