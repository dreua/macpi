package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MacPiMain implements DeviceProvider {
	
	public static final String[] WAITING_MESSAGE = new String[] {
					"Plug me in please", 
					"then I will show you", 
					"the MAC address of", 
					"the connected Device"};
	
	private Device d;

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
			Output.print(WAITING_MESSAGE);
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
			MacPiServer mps = new MacPiServer((DeviceProvider)this);
			mps.start();
			while ((line = in.readLine()) != null) {
				if (line.startsWith("lease")) {
					if (d != null) {
						d.remove();
					}
					d = new Device(line.split(" ")[1]);
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
