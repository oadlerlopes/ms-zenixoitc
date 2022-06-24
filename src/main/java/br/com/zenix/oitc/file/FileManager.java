package br.com.zenix.oitc.file;

import org.bukkit.Bukkit;
import org.bukkit.World;

import br.com.zenix.oitc.manager.Management;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class FileManager extends Management {

	private static final World world = Bukkit.getWorld("world");

	public FileManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public static World getWorld() {
		return world;
	}

}
