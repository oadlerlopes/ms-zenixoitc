package br.com.zenix.oitc.player.gamer.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.bukkit.player.detection.events.AsyncPreDamageEvent;
import br.com.zenix.oitc.game.custom.GamerHitEntityEvent;
import br.com.zenix.oitc.game.custom.MinigameListener;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class DamageManager extends MinigameListener {

	public static final HashMap<Material, Double> damageMaterial = new HashMap<>();

	public DamageManager() {

		damageMaterial.put(Material.DIAMOND_SWORD, 4.0D);
		damageMaterial.put(Material.IRON_SWORD, 3.5D);
		damageMaterial.put(Material.STONE_SWORD, 2.5D);
		damageMaterial.put(Material.WOOD_SWORD, 4.5D);
		damageMaterial.put(Material.GOLD_SWORD, 2.0D);

		damageMaterial.put(Material.DIAMOND_AXE, 5.0D);
		damageMaterial.put(Material.IRON_AXE, 4.0D);
		damageMaterial.put(Material.STONE_AXE, 3.0D);
		damageMaterial.put(Material.WOOD_AXE, 2.0D);
		damageMaterial.put(Material.GOLD_AXE, 2.0D);

		damageMaterial.put(Material.DIAMOND_PICKAXE, 4.0D);
		damageMaterial.put(Material.IRON_PICKAXE, 3.0D);
		damageMaterial.put(Material.STONE_PICKAXE, 2.0D);
		damageMaterial.put(Material.WOOD_PICKAXE, 1.0D);
		damageMaterial.put(Material.GOLD_PICKAXE, 1.0D);

		for (Material mat : Material.values()) {
			if (damageMaterial.containsKey(mat)) {
				continue;
			}
			damageMaterial.put(mat, 1.0D);
		}
	}

	@EventHandler
	public void onLava(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.LAVA) {
			e.setDamage(4.0D);
		}
	}

	@EventHandler
	public void onAsyncPreDamageEvent(AsyncPreDamageEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getDamager();

		if (player.hasMetadata("custom")) {
			player.removeMetadata("custom", getManager().getPlugin());
			return;
		}

		double damage = 1.0D;

		ItemStack itemStack = player.getItemInHand();

		if (itemStack != null) {
			damage = damageMaterial.get(itemStack.getType());
			if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
				damage += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}

		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				int amplifier = effect.getAmplifier() + 1;
				damage += (amplifier * 2);
			} else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
				damage -= (effect.getAmplifier() + 1);
			}
		}

		if (event.getEntity() instanceof LivingEntity) {
			GamerHitEntityEvent gamerEvent = new GamerHitEntityEvent(player, (LivingEntity) event.getEntity(), damage);
			Bukkit.getPluginManager().callEvent(gamerEvent);
			if (gamerEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			} else {
				LivingEntity le = (LivingEntity) event.getEntity();
				if (le.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					for (PotionEffect effect : le.getActivePotionEffects()) {
						if (!effect.getType().equals(PotionEffectType.WEAKNESS)) {
							continue;
						}
						gamerEvent.setDamage(gamerEvent.getDamage() + (effect.getAmplifier() + 1));
					}
				}
				if (player.hasPermission("*")) {
					damage = gamerEvent.getDamage() + 1;
				} else {
					damage = gamerEvent.getDamage();
				}

			}
		}
		event.setDamage(damage);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHitEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getDamager();

		if (player.hasMetadata("custom")) {
			player.removeMetadata("custom", getManager().getPlugin());
			return;
		}

		double damage = 1.0D;

		ItemStack itemStack = player.getItemInHand();

		if (itemStack != null) {
			damage = damageMaterial.get(itemStack.getType());
			if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
				damage += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}

		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				int amplifier = effect.getAmplifier() + 1;
				damage += (amplifier * 2);
			} else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
				damage -= (effect.getAmplifier() + 1);
			}
		}

		if (event.getEntity() instanceof LivingEntity) {
			GamerHitEntityEvent gamerEvent = new GamerHitEntityEvent(player, (LivingEntity) event.getEntity(), damage);
			Bukkit.getPluginManager().callEvent(gamerEvent);
			if (gamerEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			} else {
				LivingEntity le = (LivingEntity) event.getEntity();
				if (le.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					for (PotionEffect effect : le.getActivePotionEffects()) {
						if (!effect.getType().equals(PotionEffectType.WEAKNESS)) {
							continue;
						}
						gamerEvent.setDamage(gamerEvent.getDamage() + (effect.getAmplifier() + 1));
					}
				}
				if (player.hasPermission("*")) {
					damage = gamerEvent.getDamage() + 1;
				} else {
					damage = gamerEvent.getDamage();
				}

			}
		}
		event.setDamage(damage);
	}
}
