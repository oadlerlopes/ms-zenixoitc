package br.com.zenix.oitc.loaders;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.event.Listener;

import br.com.zenix.core.master.utilitaries.loader.Getter;
import br.com.zenix.oitc.game.custom.MinigameCommand;
import br.com.zenix.oitc.game.custom.MinigameListener;
import br.com.zenix.oitc.manager.Management;
import br.com.zenix.oitc.manager.Manager;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ClassLoader extends Management {

	public ClassLoader(Manager manager) {
		super(manager, "ClassLoader");
	}

	public boolean initialize() {
		return load();
	}

	public boolean load() {
		getLogger().log("Starting trying to load all the classes of commands and listeners of the plugin.");

		for (Class<?> classes : Getter.getClassesForPackage(getManager().getPlugin(), "br.com.zenix.oitc")) {
			try {
				if (MinigameCommand.class.isAssignableFrom(classes) && classes != MinigameCommand.class) {
					MinigameCommand command = (MinigameCommand) classes.newInstance();
					if (command.enabled) {
						((CraftServer) Bukkit.getServer()).getCommandMap().register(command.getName(), command);
					}
					getLogger().debug("The command " + command.getName() + "(" + command.getDescription() + ") its " + (command.enabled ? "enabled and loaded correcly" : "disabled and not loaded") + "!");
				}
			} catch (Exception exception) {
				getLogger().error("Error to load the command " + classes.getSimpleName() + ", stopping the process!", exception);
				return false;
			}
			try {
				Listener listener = null;
				if (!Listener.class.isAssignableFrom(classes)) {
					continue;
				} else if (classes.getSimpleName().equals("Timer")) {
					continue;
				} else if (classes.getSimpleName().equals("")) {
					continue;
				} else if (MinigameCommand.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor().newInstance();
				} else if (MinigameListener.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor().newInstance();
				} else {
					listener = (Listener) classes.getConstructor(Manager.class).newInstance(getManager());
				}

				Bukkit.getPluginManager().registerEvents(listener, getManager().getPlugin());
				getLogger().debug("The listener " + listener.getClass().getSimpleName() + " was loaded correcly!");

			} catch (Exception exception) {
				getLogger().error("Error to load the listener " + classes.getSimpleName() + ", stopping the process!", exception);
				return false;
			}
		}
		return true;
	}

}
