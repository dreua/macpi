package de.dauersolutions.macpi.server;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class MacPiServer extends Thread {
	
	
	
	private DeviceProvider deviceProvider;

	public MacPiServer(DeviceProvider deviceProvider) {
		this.deviceProvider = deviceProvider;
		setDaemon(true);
		setName("MacPiServer-Thread");
	}

	public void run() {
		try {
			ServerSocket seso = new ServerSocket(9099, 0, InetAddress.getByName(null));
			Socket so;
			while ((so = seso.accept()) != null) {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
				out.write("supermac:" + deviceProvider.getDevice().mac + "\n");
				out.flush();
				out.close();
				so.close();
			}
			seso.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
