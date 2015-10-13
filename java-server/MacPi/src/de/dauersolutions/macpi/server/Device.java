package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Device extends Thread {
	String ip = null;
	String ipPrint = null;
	String mac = null;
	String macPrint = null;
	
	boolean terminate = false;
	boolean isReachable = false;
	boolean isIcmpPingable = false;

	public void remove() {
		terminate = true;
		Output.print(new String[] {"Waiting for","device"});;
		
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
		
//		macPrint = mac.substring(0,2) + mac.substring(3, 3+2+3*4);
		macPrint = "Mac:" + mac.replace(":", "").toUpperCase();
//		macPrint = macPrint.toUpperCase();
		print();

		new Thread(new Runnable() {
			private Device d;
			public Runnable setup(Device d) {
				this.d = d;
				setDaemon(true);
				return this;
			}
			@Override
			public void run() {
				InetAddress a;
				while (!d.terminate) {
//					System.out.println("testing reachable");
					try {
						a = InetAddress.getByName(d.ip);
						try {
							if (a.isReachable(500)) {
								d.isReachable = true;
							} else {
								d.isReachable = false;
							}
//							d.print();
							Output.reachable(d.isReachable);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
				}
				
			}
			
		}.setup(this)).start();
		
		if (!isAlive())
			start();
		
	}
	
	public void run() {
		// icmp ping here
		while (!terminate) {
//			System.out.println("Testin icmp ping");
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
				Thread.sleep(Math.min(500, 
						Math.max(50, 500 - (System.currentTimeMillis() - start))));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isIcmpPingable = gotPing;
//			print();
			Output.icmp(gotPing);
		}
	}
	
	public void print() {
		Output.print(macPrint, (isReachable ? "R":".") + 
				(isIcmpPingable ? "P" : ".")+ " "+ipPrint);
	}

}
