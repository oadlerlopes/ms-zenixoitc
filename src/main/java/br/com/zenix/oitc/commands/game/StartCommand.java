package br.com.zenix.oitc.commands.game;

import org.bukkit.command.CommandSender;

import br.com.zenix.oitc.game.custom.MinigameCommand;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class StartCommand extends MinigameCommand {

	public StartCommand() {
		super("start", "Forçar o inicio da jogo");
	}
 
	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "start")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (!getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§a§lSTART §fVocê não pode §a§lINICIAR§f a jogo depois que a mesma iniciou.");
			return false;
		}

		getManager().getGameManager().getTimer().startGame();
		commandSender.sendMessage("§a§lSTART §fVocê §a§lINICIOU§f a §2§lPARTIDA!");
		return true;
	}
}
