package de.dauersolutions.macpi.server.ping;

import de.dauersolutions.macpi.server.Device;

public abstract class PingThread extends Thread {
	
	protected Device device;
	
	public PingThread(Device device) {
		this.device = device;
		this.setDaemon(true);
	}

}
