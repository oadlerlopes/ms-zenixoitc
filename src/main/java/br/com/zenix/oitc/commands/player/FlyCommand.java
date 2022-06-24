package br.com.zenix.oitc.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.oitc.game.custom.MinigameCommand;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class FlyCommand extends MinigameCommand {

	public FlyCommand() {
		super("fly", "Entre no modo de voo.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "fly")) {
			sendPermissionMessage(commandSender);
			return true;
		}
		if (args.length != 0) {
			commandSender.sendMessage("§e§lFLY §fUse: /fly");
			return false;
		}

		Gamer g = getManager().getGamerManager().getGamer((Player) commandSender);
		if (!getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§e§lFLY §fVocê §5§lNAO PODE USAR§f isto agora.");
			return false;
		}

		if (g.isOnPvpPregame()) {
			commandSender.sendMessage("§e§lFLY §fVocê não pode usar isto agora.");
			return false;
		}

		g.getPlayer().setAllowFlight(!g.getPlayer().getAllowFlight());
		g.sendMessage("§e§lFLY §fVocê "
				+ (g.getPlayer().getAllowFlight() ? "§a§lhabilitou".toUpperCase() : "§c§ldesativou".toUpperCase())
				+ "§f o §6§lFLY");
		return true;
	}
}
