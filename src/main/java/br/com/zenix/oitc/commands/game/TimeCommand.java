package br.com.zenix.oitc.commands.game;

import org.bukkit.command.CommandSender;

import br.com.zenix.oitc.game.custom.MinigameCommand;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class TimeCommand extends MinigameCommand {

	public TimeCommand() {
		super("tempo", "Mude o tempo de jogo!");
	}

	public boolean execute(CommandSender commandSender, String cmd, String[] args) {
		if (!hasPermission(commandSender, "tempo")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (args.length != 1) {
			commandSender.sendMessage("§3§lTEMPO §fUse: /tempo <segundos/minutos>");
			return false;
		}

		long time;
		try {
			time = getManager().getUtils().parseDateDiff(args[0], true);
		} catch (Exception e) {
			commandSender.sendMessage("§3§lTEMPO §fA sua sintaxe está incorreta!");
			return false;
		}

		time = (time - System.currentTimeMillis()) / 1000;

		getManager().getGameManager().setGameTime((int) time);
		commandSender.sendMessage("§3§lTEMPO §fVocê alterou o tempo do §e§lJOGO§f para "
				+ getManager().getUtils().formatTime((int) time));
		return true;
	}

}
