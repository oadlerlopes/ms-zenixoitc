package br.com.zenix.oitc.player.gamer;

import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.zenix.core.bukkit.player.account.Account;
import br.com.zenix.core.master.data.handler.type.DataType;
import br.com.zenix.oitc.OITC;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Gamer {

	private final Account account;

	private Player player;

	private GamerMode mode;

	private UUID uuid;
	private boolean blocked;

	private boolean allow;
	private int timer;

	private int gameKills, fighting, lifes;
	private boolean online, isloaded, chatspec, itemsGive, pvpPregame, specs;

	public Gamer(Account account) {
		this.account = account;
		this.uuid = account.getUniqueId();

		mode = GamerMode.LOADING;

		gameKills = 0;
		fighting = 0;
		lifes = 5;

		isloaded = false;
		online = true;
		pvpPregame = false;
		chatspec = false;
		itemsGive = false;
		pvpPregame = false;
		blocked = false;
		specs = false;

		timer = 0;
		allow = true;
	}

	public int getTimer() {
		return timer;
	}

	public boolean isAllow() {
		return allow;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setAllow(boolean allow) {
		this.allow = allow;
	}

	public boolean isSpecs() {
		return specs;
	}

	public void setSpecs(boolean specs) {
		this.specs = specs;
	}

	public void updatePlayer(Player player) {
		this.player = player;
		getAccount().updatePlayer(player);
	}

	public void update() {
		getManager().getGamerManager().updateGamer(this);
	}

	public Account getAccount() {
		return account;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public Boolean isFighting() {
		return fighting > 0;
	}

	public Boolean isLoaded() {
		return isloaded;
	}

	public Boolean isOnline() {
		return online;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Integer getGameKills() {
		return gameKills;
	}

	public GamerMode getMode() {
		return mode;
	}

	public Boolean isSpectating() {
		if (getManager().getAdminManager().isAdmin(player))
			return true;
		return mode == GamerMode.SPECTING;
	}

	public Boolean isAlive() {
		return mode == GamerMode.ALIVE;
	}

	public Boolean isDead() {
		return mode == GamerMode.DEAD;
	}

	public Player getPlayer() {
		return player;
	}

	public int getLifes() {
		return lifes;
	}

	public Boolean isOnPvpPregame() {
		return pvpPregame;
	}

	public Boolean inChatSpec() {
		return chatspec;
	}

	public Boolean getItemsGive() {
		return itemsGive;
	}

	public Manager getManager() {
		return OITC.getManager();
	}

	public String getNick() {
		return getAccount().getNickname();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public void setFighting(Integer fighting) {
		this.fighting = fighting;
	}

	public void setGameKills(Integer gameKills) {
		this.gameKills = gameKills;

		if (gameKills > getAccount().getDataHandler().getValue(DataType.HG_MOST_KILLSTREAK).getValue())
			getAccount().getDataHandler().getValue(DataType.HG_MOST_KILLSTREAK).setValue(gameKills);
	}

	public void setLoaded(Boolean bool) {
		this.isloaded = bool;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public void setLifes(int lifes) {
		this.lifes = lifes;
	}

	public void setMode(GamerMode mode) {
		this.mode = mode;
	}

	public void sendMessage(String string) {
		getPlayer().sendMessage(string);
	}

	public void refreshFighting() {
		if (fighting > 0) {
			fighting -= 1;
		}
	}

	public void setItemsGive(Boolean itemsGive) {
		this.itemsGive = itemsGive;
	}

	public void setPvpPregame(Boolean pvpPregame) {
		this.pvpPregame = pvpPregame;
	}

	public void setChatspec(Boolean chatspec) {
		this.chatspec = chatspec;
	}
}
