package br.com.zenix.oitc.game.handler.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import br.com.zenix.oitc.game.custom.MinigameListener;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class InvencibilityListener extends MinigameListener {

	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		if (getManager().getGameManager().isGame())
			if (event.getEntity() instanceof Player)
				event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player)
			if (getManager().getGameManager().isGame())
				event.setCancelled(true);
	}
}
