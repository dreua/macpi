package de.dauersolutions.macpi.server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Output {
	
	private static Output instance = null;
	
	public static Output getInstance() {
		if (instance == null) {
			instance = new Output();
		}
		return instance;
	}
	
	Process p;
	BufferedWriter pout;
	private boolean icmpLower;
	private boolean reachableLower;
	
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
//			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//			String line;
//			while ((line = in.readLine()) != null) {
//				pout.write(line + "\n");
//				pout.flush();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void print(String... s) {
		for (int i = 0; i < s.length; i++) {
			if (s[i].length() > 16) {
				s[i] = s[i].substring(0, 16);
				System.out.println("string too long, truncating");
			} else {
				while (s[i].length() < 16) {
					s[i] = s[i]+" ";
				}
			}
			
		}
		
		System.out.println(System.currentTimeMillis());
		
		System.out.println("+----------------+");

		System.out.println("|"+s[0] + "|");
		System.out.println("|"+s[1] + "|");

		System.out.println("+----------------+");
		getInstance().toLcd(s);
	}

	private void toLcd(String[] s) {
		try {
			pout.write("lcd.clear()\n");
			pout.write("lcd.message(\""+s[0]+"\\n"+s[1]+"\")\n");
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
		}
		
	}

	public static void icmp(boolean gotPing) {
		Output.getInstance().icmpE(gotPing);
		
	}

	private void icmpE(boolean gotPing) {
		char c;
		if (icmpLower) {
			icmpLower = false;
			c = 'P';
		} else {
			icmpLower = true;
			c = 'p';
		}
		if (!gotPing)
			c = '.';
		lcdWriteCharAt(1, 1, c);
	}

	public static void reachable(boolean isReachable) {
		Output.getInstance().reachalbeE(isReachable);
	}

	private void reachalbeE(boolean isReachable) {
		char c;
		if (reachableLower) {
			reachableLower = false;
			c = 'R';
		} else {
			reachableLower = true;
			c = 'r';
		}
		if (!isReachable)
			c = '.';
		lcdWriteCharAt(0, 1, c);

	}
	
	

}
