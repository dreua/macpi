package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Device extends Thread {
	
	public static final int PING_TIMEOUT_MILLIS = 100;
	
	String ip = null;
	String ipPrint = null;
	String mac = null;
	String macPrint = null;
	
	boolean terminate = false;
	boolean isIcmpPingable = false;

	public void remove() {
		terminate = true;
		Output.print(new String[] {"Waiting for","device"});
		
	}
	
	public Device() {
		setDaemon(true);
	}

	public void setIp(String ip) {
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
		if (!isAlive())
			start();
	}
	
	public void run() {
		// icmp ping here
		while (!terminate) {
			boolean gotPing = false;
			try {
				long start = System.currentTimeMillis();
				Process p = Runtime.getRuntime().exec("ping -c 1 -w 2 " + ip);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					//System.out.println(line);
					if (line.contains("bytes from")) {
						gotPing = true;
					}
				}
				Thread.sleep(Math.min(PING_TIMEOUT_MILLIS, 
						Math.max(50, PING_TIMEOUT_MILLIS - 
								(System.currentTimeMillis() - start))));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isIcmpPingable = gotPing;
//			print();
			Output.icmp(gotPing);
		}
	}
	
	public void print() {
		Output.print("Connected MAC:", 
				macPrint + "-R",
				"ARP-Ping:",
				"ICMP-Ping:");
	}

}
