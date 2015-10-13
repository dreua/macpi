package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MacPiMain implements DeviceProvider {
	
	Device d;

	public static void main(String[] args) throws Throwable {
		new MacPiMain();
	}
	
	public MacPiMain() throws IOException {
//		System.out.println("Goodbye World");
		Output.print(new String[] {"Waiting for device", "..."});
		Process tail = Runtime.getRuntime().exec(
				"tail -n0 -f /var/lib/dhcp/dhcpd.leases");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				tail.getInputStream()));
		
		String line;
		d = new Device();
		MacPiServer mps = new MacPiServer((DeviceProvider)this);
		mps.start();
		while ((line = in.readLine()) != null) {
			if (line.startsWith("lease")) {
				d.remove();
				d = new Device();
				d.setIp(line.split(" ")[1]);
			}
			String he = "hardware ethernet";
			if (line.contains(he)) {
				d.setMac(line.substring(line.indexOf(he)+he.length()+1,
						line.length()-1));
			}
		}

	}

	@Override
	public Device getDevice() {
		return d;
	}

}
