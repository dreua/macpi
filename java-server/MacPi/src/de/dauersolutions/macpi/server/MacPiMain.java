package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MacPiMain implements DeviceProvider {
	
	Device d;

	public static void main2(final String[] args) throws InterruptedException {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutdown hook ran!");
			}
		});

		while (true) {
			Thread.sleep(1000);
		}
	}

	public static void main(String[] args) throws Throwable {
		new MacPiMain();
	}
	
	public MacPiMain() {
		BufferedReader in = null;
		final Process tail;
		try {
			Output.print(new String[] {"Waiting for device", "..."});
			tail = Runtime.getRuntime().exec(
					"tail -n0 -f /var/lib/dhcp/dhcpd.leases");
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("Killing tail");
					if (tail != null) {
						tail.destroy();
						tail.destroyForcibly();
					}
				}
			});
			in = new BufferedReader(new InputStreamReader(
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public Device getDevice() {
		return d;
	}

}
