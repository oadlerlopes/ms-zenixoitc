package br.com.zenix.oitc;

import java.io.File;

import br.com.zenix.core.bukkit.Core;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class OITC extends Core {

	private static Manager manager;
	private boolean TEST_SERVER;

	public void onEnable() {
		super.onEnable();

		if (!isCorrectlyStarted())
			return;

		manager = new Manager(this);
	}

	public void onDisable() {
		super.onDisable();
	}

	public void onLoad() {
		super.onLoad();
	}

	public boolean TEST_SERVER() {
		return TEST_SERVER;
	}

	public static Manager getManager() {
		return manager;
	}
	
	public void deleteDir(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(file, children[i]));
			}
		}
		file.delete();
	}

}
