package br.com.zenix.oitc.player.admin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.oitc.game.custom.MinigameListener;


/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class AdminListener extends MinigameListener {

	@EventHandler
	private void onCancelBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (getManager().getAdminManager().isAdmin(player) && !player.hasPermission("core.cmd.adminplus"))
			event.setCancelled(true);
	}

	@EventHandler
	private void onInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player player = event.getPlayer();

			if (getManager().getAdminManager().isAdmin(player)) {
				Player clicked = (Player) event.getRightClicked();
				ItemStack item = player.getInventory().getItemInHand();

				if (item.getType().equals(Material.AIR)) {
					player.performCommand("invsee " + clicked.getName());
				}
			}
		}
	}
}
