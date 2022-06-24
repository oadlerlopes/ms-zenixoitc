package br.com.zenix.oitc.game.handler;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum GameState {

	PREGAME("Pré Jogo"),
	GAME("Jogo"),
	FINAL("Final"),
	WINNING("Vitória");

	public String name;

	GameState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
