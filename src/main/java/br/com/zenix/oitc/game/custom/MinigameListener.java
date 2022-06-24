package br.com.zenix.oitc.game.custom;

import org.bukkit.event.Listener;

import br.com.zenix.oitc.OITC;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class MinigameListener implements Listener {

	public Manager getManager() {
		return OITC.getManager();
	}

}
