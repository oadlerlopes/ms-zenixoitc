package br.com.zenix.oitc.player.gamer.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.bukkit.player.item.ItemBuilder;
import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.game.handler.LoadItems;
import br.com.zenix.oitc.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Spectators extends MinigameListener {

	private static final ItemBuilder itemBuilder = new ItemBuilder(Material.AIR);

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (getManager().getGamerManager().getGamer(event.getPlayer()) != null
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()
				&& event.getAction().name().contains("RIGHT")) {
			if (itemBuilder.checkItem(event.getItem(),
					LoadItems.SPEC.getItem(0).getStack().getItemMeta().getDisplayName())) {
				event.setCancelled(true);

				Inventory inventory = Bukkit.createInventory(event.getPlayer(), 54, "Jogadores");
				for (Gamer g : getManager().getGamerManager().getAliveGamers()) {
					if (g.getPlayer().isOnline() && inventory.firstEmpty() != 1) {
						itemBuilder.setMaterial(Material.SKULL_ITEM).setDurability(3)
								.setName("§r"
										+ getManager().getCoreManager().getTagManager().getDisplayName(g.getPlayer()))
								.setDescription("§bAbates: §f" + g.getGameKills() + "\n§bPvP: §f"
										+ (g.isFighting() ? "Lutando" : "Parado"))
								.build(inventory, inventory.firstEmpty());
					}
				}

				event.getPlayer().openInventory(inventory);

			} else if (itemBuilder.checkItem(event.getItem(),
					LoadItems.SPEC.getItem(1).getStack().getItemMeta().getDisplayName())) {
				event.setCancelled(true);

				List<Gamer> pvp = new ArrayList<>();
				for (Gamer g : getManager().getGamerManager().getAliveGamers()) {
					if (g.isFighting() && g.isAlive() && g.getPlayer().isOnline()) {
						pvp.add(g);
					}
				}

				if (pvp.size() == 0) {
					event.getPlayer().sendMessage("§b§lLMS §fNenhum player está §3§lLUTANDO");
					return;
				}

				Gamer random = pvp.get(getManager().getRandom().nextInt(pvp.size()));
				event.getPlayer().teleport(random.getPlayer().getLocation());
				event.getPlayer().sendMessage("§b§lLMS §fTeleportado para §3§l" + random.getPlayer().getName());
			}
		}
	}

	private static final ArrayList<UUID> cantTouch = new ArrayList<>();

	@EventHandler
	public void onRight(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Vehicle
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		} else if (event.getRightClicked() instanceof Player
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			if (event.getPlayer().getItemInHand().getType() == Material.AIR) {
				Player player = (Player) event.getRightClicked();
				Player p = event.getPlayer();
				p.chat("/invsee " + player.getName());
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (cantTouch.contains(event.getWhoClicked().getUniqueId())) {
			if (event.getWhoClicked().hasPermission("oitc.staff")) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (cantTouch.contains(event.getPlayer().getUniqueId())) {
			cantTouch.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCheck(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (getManager().getGamerManager().getGamer(player).isSpectating()) {
			if (event.getWhoClicked().hasPermission("oitc.staff")) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player
				&& getManager().getGamerManager().getGamer(event.getEntity().getUniqueId()) != null
				&& getManager().getGamerManager().getGamer(event.getEntity().getUniqueId()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player
				&& getManager().getGamerManager().getGamer((Player) event.getEntity()).isSpectating()) {
			event.setCancelled(true);
			event.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player p = (Player) event.getTarget();
			if (p.hasPermission("oitc.staff")) {
				return;
			}
			if (getManager().getGamerManager().getGamer(p).isSpectating()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			Block b = event.getClickedBlock();

			if (b.getState() instanceof DoubleChest || b.getState() instanceof Chest || b.getState() instanceof Hopper
					|| b.getState() instanceof Dispenser || b.getState() instanceof Furnace
					|| b.getState() instanceof Beacon) {
				if (!event.getPlayer().hasPermission("oitc.staff")) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (getManager().getGamerManager().getGamer(player).isSpectating()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.getPlayer().hasPermission("oitc.staff")) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("oitc.staff")) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().hasPermission("oitc.staff")) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventory(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equalsIgnoreCase("Jogadores")) {
			event.setCancelled(true);

			Player p = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();

			if (item != null && item.getType() == Material.SKULL_ITEM && item.getDurability() == 3 && item.hasItemMeta()
					&& item.getItemMeta().hasDisplayName()) {
				String display = item.getItemMeta().getDisplayName();
				Player player = null;

				for (Gamer g : getManager().getGamerManager().getAliveGamers()) {
					Player o = g.getPlayer();
					if (ChatColor.stripColor(o.getName()).equalsIgnoreCase(ChatColor.stripColor(display))) {
						player = o;
						break;
					}
				}

				p.closeInventory();

				if (player == null) {
					p.sendMessage("§b§lLMS §fEste não está no §3§lSERVIDOR");
				} else {
					p.teleport(player.getLocation());
					p.sendMessage("§b§lLMS §fTeleportado para §3§l" + display);
				}
			}
		}
	}
}
