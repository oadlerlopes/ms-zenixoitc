package br.com.zenix.oitc.manager;

import java.util.logging.Level;

import br.com.zenix.core.master.logger.FormattedLogger;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class Management {

	private final String name;
	private final FormattedLogger formattedLogger;
	private final Manager manager;
	private boolean correctlyStart = true;

	public Management(Manager manager) {
		this.name = getClass().getSimpleName().replace("Manager", "");
		this.manager = manager;
		this.formattedLogger = new FormattedLogger(manager.getLogger(), name);

		formattedLogger.log("Trying to start the '" + name + "' handler.");

		checkStart(initialize());
	}

	public Management(Manager manager, String name) {
		this.name = name;
		this.manager = manager;
		this.formattedLogger = new FormattedLogger(manager.getLogger(), name);

		formattedLogger.log("Trying to start the '" + name + "' manager.");

		checkStart(initialize());
	}

	public Management(Management manager, String name) {
		this.name = name;
		this.manager = manager.getManager();
		this.formattedLogger = new FormattedLogger(manager.getLogger(), name);

		formattedLogger.log("Trying to start the '" + name + "' manager.");

		checkStart(initialize());
	}

	public abstract boolean initialize();

	protected boolean checkStart(boolean bool) {
		if (bool) {
			getLogger().log("The " + name + " manager has been started correctly.");
		} else {
			getLogger().log(Level.SEVERE, "The '" + name + "' manager has been not started correctly, stopping the server.");
			getLogger().log(Level.SEVERE, "The server is going to stop because the manager '" + name + "' it was not started.");
			getManager().getPlugin().getServer().shutdown();
			correctlyStart = false;
		}
		return bool;
	}

	public boolean correctlyStart() {
		return correctlyStart;
	}

	public Manager getManager() {
		return manager;
	}

	protected String getName() {
		return name;
	}

	public FormattedLogger getLogger() {
		return formattedLogger;
	}

}
