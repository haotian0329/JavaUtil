package wanghao.util.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class SocketClient {
	private String hostname=null;
	private InetAddress address=null;
	private int port;
	private int timeOutMS=15000;//连接设置的默认超时时间为15S
	protected Socket socket=null;
	
	/**
	 * 由子类实现的抽象方法，与服务端具体的交互操作
	 * @param encode 
	 */
	public abstract void operation(String encode);
	
	public SocketClient(String hostname, int port) {
		this.hostname=hostname;
		this.port=port;
	}
	
	public SocketClient(String hostname, int port, int timeOutMS) {
		this(hostname,port);
		this.timeOutMS=timeOutMS;
	}
	
	public SocketClient(InetAddress address, int port) {
		this.address=address;
		this.port=port;
	}
	
	public SocketClient(InetAddress address, int port, int timeOutMS) {
		this(address,port);
		this.timeOutMS=timeOutMS;
	}
	
	/**
	 * 客户端建立连接
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void connect() throws UnknownHostException, IOException{
		if (hostname != null) {
			socket = new Socket(hostname, port);
		} else if (address != null) {
			socket = new Socket(address, port);
		} else {
			throw new RuntimeException(
					"one of param hostname and address should not be null!");
		}
		socket.setSoTimeout(timeOutMS);
	}
	
	/**
	 * 客户端断开连接
	 */
	private void disconnect(){
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printInfo(){
		System.out.println("Connected to " + socket.getInetAddress() 
	            + " on port "  + socket.getPort() + " from port " 
	            + socket.getLocalPort() + " of " 
	            + socket.getLocalAddress());
	}
	
	/**
	 * 实际工作流程
	 * @param encode
	 */
	protected void work(String encode){
		try {
			connect();
			//还可以设置客户端socket选项
			printInfo();
			operation(encode);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			disconnect();
		}
	}
	
}
