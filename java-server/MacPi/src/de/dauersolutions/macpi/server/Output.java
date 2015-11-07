package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Output {
	
	private static Output instance = null; 
	
	public static final int DISPLAY_WIDTH = 20;
	public static final int DISPLAY_HEIGHT = 4;
	
	public static Output getInstance() {
		if (instance == null) {
			instance = new Output();
		}
		return instance;
	}
	
	Process p;
	BufferedWriter pout;
	private boolean icmpLower;
	private boolean arpLower;
	
	private Output() {
		System.out.println("output-init-bla");
		try {
			p = Runtime.getRuntime().exec("sudo python lcd_driver.py");
			final BufferedReader pin = new BufferedReader(new InputStreamReader(p.getInputStream()));
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					String line;
					try {
						System.out.println("starting input");
						while ((line = pin.readLine()) != null) {
							System.out.println("<pyout>" + line);
						}
						System.out.println("End of input");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			t.setDaemon(true);
			t.start();
			final BufferedReader pine = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			Thread t2 = new Thread(new Runnable() {
				@Override
				public void run() {
					String line;
					try {
						while ((line = pine.readLine()) != null) {
							System.out.println("<pyout-error>" + line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			t2.setDaemon(true);
			t2.start();
			pout = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void print(String... s) {
		if (s.length > DISPLAY_HEIGHT) {
			System.out.println("Too many Lines in output");
		}
		for (int i = 0; i < s.length; i++) {
			if (s[i].length() > DISPLAY_WIDTH) {
				s[i] = s[i].substring(0, DISPLAY_WIDTH);
				System.out.println("string too long, truncating");
			} else {
				while (s[i].length() < DISPLAY_WIDTH) {
					s[i] = s[i]+" ";
				}
			}
			
		}
		System.out.println("Output example:");
		for (int i = 0; i < s.length; i++) {
			System.out.println("|"+s[i] + "|");
		}

		getInstance().toLcd(s);
	}
	
	private void clearLcd() throws IOException {
		pout.write("clear()\n");
		pout.flush();
	}
	
	private void toLcd(String[] s) {
		try {
			clearLcd();
			if (s.length == 0) {
				return;
			}
			for (int i = 0; i < s.length; i++) {
				pout.write("w(\"" + s[i] + "\", " + i + ")\n");
				System.out.println("Writing: " + s[i]);
			}
			pout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	private void lcdWriteCharAt(int col, int row, String c) {
		try {
			pout.write("c(\"" + c + "\","+row+","+col+")\n");
//			pout.write("lcd.message(\""+c+"\")\n");
			pout.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error writing to python");
		}
		
	}

	public static void icmp(boolean gotPing) {
		Output.getInstance().icmpE(gotPing);
		
	}

	public static void arp(boolean gotPing) {
		Output.getInstance().arpE(gotPing);
		
	}

	private void arpE(boolean gotPing) {
		String c;
		if (arpLower) {
			arpLower = false;
			c = "|";
		} else {
			arpLower = true;
			c = "-";
		}
		if (!gotPing)
			c = " ";
		lcdWriteCharAt(11, 2, c);
	}

	private void icmpE(boolean gotPing) {
		String c;
		if (icmpLower) {
			icmpLower = false;
			c = "|";
		} else {
			icmpLower = true;
			c = "-";
		}
		if (!gotPing)
			c = " ";
		lcdWriteCharAt(11, 3, c);
	}


}
