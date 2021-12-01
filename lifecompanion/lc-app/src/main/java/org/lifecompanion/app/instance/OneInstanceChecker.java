/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.app.instance;

import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to detect if there is a double launch for LifeCompanion.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum OneInstanceChecker {
	INSTANCE;
	private static final int PORT = 8645;//Not assigned port on most of computers
	private static final String RMI_NAME = "LifeCompanion.Double.Instance.Detector";

	private final static Logger LOGGER = LoggerFactory.getLogger(OneInstanceChecker.class);

	private DoubleLaunchListener doubleLaunchListenerImpl;
	private DoubleLaunchListener rmiObject;
	private Registry registry;

	public boolean checkDoubleRun(DoubleLaunchListener impl) {
		boolean doubleRun = false;
		try {
			//First call, creation will not fail and we will be able to bind the remote server
			this.registry = LocateRegistry.createRegistry(OneInstanceChecker.PORT);
			this.doubleLaunchListenerImpl = impl;
			this.rmiObject = (DoubleLaunchListener) UnicastRemoteObject.exportObject(this.doubleLaunchListenerImpl, 0);
			this.registry.rebind(OneInstanceChecker.RMI_NAME, this.rmiObject);
			OneInstanceChecker.LOGGER.info("RMI registry for double run detection initialized");
		} catch (RemoteException e) {
			if (e.getCause() != null && e.getCause() instanceof BindException) {
				OneInstanceChecker.LOGGER.warn(
						"Couldn't create RMI server because a RMI server already exist on this port : {}, will check if a existing instance is running",
						e.getCause().getMessage());
				doubleRun = this.checkExistingRunningInstance();
			} else {
				OneInstanceChecker.LOGGER.error("Couldn't create RMI server", e);
			}
		}
		return doubleRun;
	}

	public void stopRmiServer() {
		stopRmiServer(true);
	}

	public void stopRmiServer(boolean forceStop) {
		try {
			OneInstanceChecker.LOGGER.info("Will try to stop RMI server");
			this.registry.unbind(OneInstanceChecker.RMI_NAME);
			boolean unexportSuccess = UnicastRemoteObject.unexportObject(this.doubleLaunchListenerImpl, true);
			boolean unexportRmi = UnicastRemoteObject.unexportObject(registry, true);
			if (unexportSuccess && unexportRmi) {
				OneInstanceChecker.LOGGER.info("RMI server stopped");
			} else if (forceStop) {
				this.forceStop();
			}
		} catch (Exception e) {
			OneInstanceChecker.LOGGER.error("Couldn't stop RMI server", e);
			if (forceStop) {
				this.forceStop();
			}
		}
	}

	private void forceStop() {
		OneInstanceChecker.LOGGER.warn("Couldn't stop the RMI server, will stop by calling exit()");
		System.exit(-1);
	}

	private boolean checkExistingRunningInstance() {
		boolean existingInstance = false;
		try {
			//Try to get existing instance and fire a message to it
			Registry registry = LocateRegistry.getRegistry(OneInstanceChecker.PORT);
			DoubleLaunchListener lookup = (DoubleLaunchListener) registry.lookup(OneInstanceChecker.RMI_NAME);
			lookup.doubleRunDetected();
			//Message sent, existing instance detected
			existingInstance = true;
		} catch (Exception e) {
			OneInstanceChecker.LOGGER.error("Wasn't able to check that a existing instance is running", e);
		}
		return existingInstance;
	}
}
