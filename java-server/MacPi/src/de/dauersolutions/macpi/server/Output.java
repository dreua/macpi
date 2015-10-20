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
	
	private Output() {
		System.out.println("output-init-bla");
		try {
			p = Runtime.getRuntime().exec("sudo python macpi/Adafruit_CharLCD.py");// TODO not portable
			final BufferedReader pin = new BufferedReader(new InputStreamReader(p.getInputStream()));
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					String line;
					try {
						while ((line = pin.readLine()) != null) {
							System.out.println("<pyout>" + line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
							System.out.println("<pyout>" + line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
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

	private void toLcd(String[] s) {
		try {
			pout.write("lcd.clear()\n");
			pout.write("lcd.message(\"");
			for (int i = 0; i < s.length; i++) {
				pout.write(s[i]);
				if (i < s.length - 1) {
					pout.write("\\n");
				}
			}
			pout.write("\")\n");
			pout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	private void lcdWriteCharAt(int col, int row, char c) {
		try {
			pout.write("lcd.setCursor("+col+", "+row+")\n");
			pout.write("lcd.message(\""+c+"\")\n");
			pout.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error writing to python");
		}
		
	}

	public static void icmp(boolean gotPing) {
		Output.getInstance().icmpE(gotPing);
		
	}

	private void icmpE(boolean gotPing) {
		char c;
		if (icmpLower) {
			icmpLower = false;
			c = '\\';
		} else {
			icmpLower = true;
			c = 'p';
		}
		if (!gotPing)
			c = '/';
		lcdWriteCharAt(11, 4, c);
	}

}
