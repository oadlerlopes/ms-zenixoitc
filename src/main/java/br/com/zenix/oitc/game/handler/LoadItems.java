package br.com.zenix.oitc.game.handler;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import br.com.zenix.core.bukkit.player.item.ItemBuilder;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum LoadItems {
	SPEC(new ItemBuilder[] { new ItemBuilder(Material.COMPASS).setName("§6Lista de Players §7(Clique para utilizar)"),
			new ItemBuilder(Material.DIAMOND_SWORD).setName("§aJogadores em PvP §7(Clique para utilizar)"), },
			new Integer[] { 2, 6 }), ;

	private ItemBuilder[] items;
	private Integer[] slots;

	LoadItems(ItemBuilder[] items, Integer[] slots) {
		this.items = items;
		this.slots = slots;
	}

	public ItemBuilder getItem(int id) {
		return id <= items.length - 1 ? items[id] : items[0];
	}

	public Integer[] getSlots() {
		return slots;
	}

	public ItemBuilder[] getItems() {
		return items;
	}

	public void build(Inventory inventory) {
		if (slots != null) {
			int id = 0;
			for (Integer slot : slots) {
				getItem(id).build(inventory, slot);
				id++;
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				getItem(i).build(inventory);
			}
		}
	}

	public void build(Player player) {
		if (slots != null) {
			int id = 0;
			for (Integer slot : slots) {
				if (getItem(id).getStack().getType() == Material.SKULL_ITEM)
					getItem(id).setSkull(player.getName());
				getItem(id).build(player.getInventory(), slot);
				id++;
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				if (getItem(i).getStack().getType() == Material.SKULL_ITEM)
					getItem(i).setSkull(player.getName());
				getItem(i).build(player.getInventory());
			}
		}
	}

}
