package de.dauersolutions.macpi.server;

import de.dauersolutions.macpi.server.ping.ArpPingThread;
import de.dauersolutions.macpi.server.ping.IcmpPingThread;
import de.dauersolutions.macpi.server.ping.PingThread;

public class Device {
	
	public static final int PING_TIMEOUT_MILLIS = 300;
	
	String ip = null;
	String ipPrint = null;
	String mac = null;
	String macPrint = null;
	
	boolean terminate = false;
	
	public Device(String ip) {
		setIp(ip);
		PingThread icmp = new IcmpPingThread(this);
		icmp.start();
		PingThread arp = new ArpPingThread(this);
		arp.start();
	}

	public void remove() {
		terminate = true;
		Output.print(MacPiMain.WAITING_MESSAGE);
		
	}
	
	private void setIp(String ip) {
		this.ip = ip;
		System.out.println("setting ip to: " + ip);
		ipPrint = ip;
		if (ip.length() > 12) {
			System.out.println("ip too long");
			ipPrint = "tooLong";
		}
		
	}

	public void setMac(String mac) {
		if (this.mac != null) return;
		this.mac = mac;
		System.out.println("setting mac to: " + mac);
		if (ip == null) {
			System.out.println("something is broken - ip is not set");
			return;
		}
		macPrint = mac.replace(":", "-").toUpperCase();
		print();
	}
	
	
	
	public void print() {
		Output.print("Connected MAC:", 
				macPrint + "-R",
				"ARP-Ping:",
				"ICMP-Ping:");
	}

	public String getIp() {
		return ip;
	}

	public boolean isTerminate() {
		return terminate;
	}

}
