package br.com.zenix.oitc.player.admin;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.manager.Manager;
import br.com.zenix.oitc.player.admin.AdminManager.AdminLevel;

public class Vanish extends MinigameListener {

	static HashMap<UUID, AdminLevel> vanished = new HashMap<>();

	private Manager manager;

	public Vanish() {
		// Auto-generated constructor stub
	}

	public Vanish(Manager manager) {
		this.manager = manager;
		// ??????????????????????????
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public void makeVanished(Player p) {
		if (p.hasPermission("commons.tag.dono")) {
			makeVanished(p, AdminLevel.DONO);
		} else if (p.hasPermission("commons.tag.developer")) {
			makeVanished(p, AdminLevel.DONO);
		} else if (p.hasPermission("commons.tag.admin")) {
			makeVanished(p, AdminLevel.ADMIN);
		} else if (p.hasPermission("commons.tag.gerente")) {
			makeVanished(p, AdminLevel.ADMIN);
		} else if (p.hasPermission("commons.tag.mod")) {
			makeVanished(p, AdminLevel.MODPLUS);
		} else if (p.hasPermission("commons.tag.trial")) {
			makeVanished(p, AdminLevel.MOD);
		}
	}

	public void makeVanished(Player p, AdminLevel level) {
		if (level.equals(AdminLevel.MOD)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(p);

				if (!player.getName().equals(p.getName())) {
					if (!player.hasPermission("commons.tag.trial")) {
						player.hidePlayer(p);
					}
				}
			}
		} else if (level.equals(AdminLevel.MODPLUS)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(p);

				if (!player.getName().equals(p.getName())) {
					if (!player.hasPermission("commons.tag.mod")) {
						player.hidePlayer(p);
					}
				}
			}
		} else if (level.equals(AdminLevel.ADMIN)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(p);

				if (!player.getName().equals(p.getName())) {
					if (!player.hasPermission("commons.tag.admin") || !player.hasPermission("commons.tag.gerente")) {
						player.hidePlayer(p);
					}
				}
			}
		} else if (level.equals(AdminLevel.DONO)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(p);

				if (!player.getName().equals(p.getName())) {
					if (!player.hasPermission("commons.tag.dono") || !player.hasPermission("commons.tag.developer")) {
						player.hidePlayer(p);
					}
				}
			}
		}
		vanished.put(p.getUniqueId(), level);
	}

	public boolean isVanished(Player p) {
		return (vanished.containsKey(p.getUniqueId()))
				&& (!((AdminLevel) vanished.get(p.getUniqueId())).equals(AdminLevel.PLAYER));
	}

	public AdminLevel getPlayerLevel(Player p) {
		return (AdminLevel) vanished.get(p.getUniqueId());
	}

	public void updateVanished() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (isVanished(p)) {
				makeVanished(p, (AdminLevel) vanished.get(p.getUniqueId()));
			} else {
				makeVisible(p);
			}
		}
	}

	public void updateVanished(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!player.getName().equals(p.getName())) {
				if (isVanished(p)) {
					if (!player.hasPermission("commons.tag.trial")) {
						if (player.canSee(p)) {
							player.hidePlayer(p);
						}
					}
				} else {
					if (!player.canSee(p)) {
						player.showPlayer(p);
					}

				}
			}
		}
	}

	public void removeVanished(Player p) {
		vanished.remove(p.getUniqueId());
	}

	public void makeVisible(Player p) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.showPlayer(p);
		}
		vanished.put(p.getUniqueId(), AdminLevel.PLAYER);
	}
}
