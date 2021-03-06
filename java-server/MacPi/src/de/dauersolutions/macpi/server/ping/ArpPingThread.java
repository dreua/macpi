package de.dauersolutions.macpi.server.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.dauersolutions.macpi.server.Device;
import de.dauersolutions.macpi.server.Output;

public class ArpPingThread extends PingThread {
	
	public ArpPingThread(Device device) {
		super(device);
	}

	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
		}
		while (!device.isTerminate()) {
			boolean gotPing = false;
			try {
				long start = System.currentTimeMillis();
				/*
				 * Perfectionistic Solution: Use fping for shorter timeout:
				 * -c 1 only send one probe
				 * by default: no rDNS lookup
				 * -t100 Wait 100ms for response
				 */
//				Process p = Runtime.getRuntime().exec("fping -c1 -t100 -q " + ip);
				Process p = Runtime.getRuntime().exec("sudo nmap -PR -sn --max-rtt-timeout=50ms -n -oG - " + device.getIp());
				BufferedReader in = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("#")) {
						continue; // comment like line
					}
					//System.out.println(line);
					if (line.contains("Status: Up")) {
						gotPing = true;
					}
				}
				Thread.sleep(Math.min(Device.PING_TIMEOUT_MILLIS, 
						Math.max(50, Device.PING_TIMEOUT_MILLIS - 
								(System.currentTimeMillis() - start))));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (gotPing) {
				Output.arp(gotPing);
			} else {
				device.remove();
			}
		}
	}

}
