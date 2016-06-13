package wanghao.util.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class DaytimeScoketClient extends SocketClient {

	public DaytimeScoketClient(String hostname, int port) {
		super(hostname, port);
	}
	
	public DaytimeScoketClient(String hostname, int port, int timeOutMS) {
		super(hostname, port, timeOutMS);
	}
	
	public DaytimeScoketClient(InetAddress address, int port) {
		super(address, port);
	}	
	
	public DaytimeScoketClient(InetAddress address, int port, int timeOutMS) {
		super(address, port, timeOutMS);
	}

	@Override
	public void operation(String encode) {
		try {
			InputStream in = socket.getInputStream();
			StringBuilder time = new StringBuilder();
			InputStreamReader reader = new InputStreamReader(in, encode);
			for (int c = reader.read(); c != -1; c = reader.read()) {
				time.append((char) c);
			}
			System.out.println(time);
		} catch (IOException ex) {
			System.err.println(ex);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String hostname="localhost";
		int port=1313;
		String encode="ASCII";
		new DaytimeScoketClient(hostname, port).work(encode);
	}

}
