package br.com.zenix.oitc.game.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

import br.com.zenix.core.bukkit.player.hologram.Hologram;
import br.com.zenix.oitc.manager.Management;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GameManager extends Management {

	private static final List<Hologram> holograms = new ArrayList<>();

	private GameState gameStage;
	private Timer timer;

	private boolean vipsCmds, isEnded;
	private int gameTime, borderTime;

	public GameManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {

		timer = new Timer(getManager());
		if (!timer.correctlyStart())
			return false;

		gameTime = getManager().getConfig().getInt("game.start");
		borderTime = 300;
		gameStage = GameState.PREGAME;

		vipsCmds = true;
		isEnded = false;

		getManager().registerListener(timer);

		World world = Bukkit.getWorld("world");
		world.setDifficulty(Difficulty.NORMAL);
		if (world.hasStorm()) {
			world.setStorm(false);
		}
		world.setTime(6000);
		world.setWeatherDuration(999999999);
		world.setGameRuleValue("doDaylightCycle", "false");

		return true;

	}

	public int getBorderTime() {
		return borderTime;
	}

	public void setBorderTime(int borderTime) {
		this.borderTime = borderTime;
	}

	public void setGameStage(GameState gameStage) {
		getLogger().log("The stage of the game is changing of " + this.gameStage + " to " + gameStage + ".");
		this.gameStage = gameStage;
	}

	public void setGameTime(Integer gameTime) {
		this.gameTime = gameTime;
	}

	public boolean isPreGame() {
		return gameStage == GameState.PREGAME;
	}

	public boolean isGame() {
		return gameStage == GameState.GAME;
	}

	public Integer getGameTime() {
		return gameTime;
	}

	public GameState getGameStage() {
		return gameStage;
	}

	public Timer getTimer() {
		return timer;
	}

	public Boolean isEnded() {
		return isEnded;
	}

	public void setEnded(Boolean isEnded) {
		this.isEnded = isEnded;
	}

	public Boolean isVipCmds() {
		return vipsCmds;
	}

	public void setVipsCmds(Boolean vipsCmds) {
		this.vipsCmds = vipsCmds;
	}

	public static List<Hologram> getHolograms() {
		return holograms;
	}

}
