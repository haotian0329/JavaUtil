package wanghao.util.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Logger;

public class DaytimeSocketServer extends SocketServer{
	
	public DaytimeSocketServer(int port, int threadsNum) {
		super(port, threadsNum);
	}
	
	public DaytimeSocketServer(int port, int threadsNum, int queueLength) {
		super(port, threadsNum,queueLength);
	}
	
	public DaytimeSocketServer(int port, int threadsNum, int queueLength, InetAddress bindAddress) {
		super(port, threadsNum,queueLength,bindAddress);
	}

	@Override
	public void operation(Socket connection, Logger auditLogger) {
		try {
			Date now = new Date();
			//先写入日志记录以防万一客户断开连接
			auditLogger.info(now+" "+connection.getRemoteSocketAddress());
			Writer out = new OutputStreamWriter(connection.getOutputStream());
			out.write(now.toString() + "\r\n");
			out.flush();
		} catch (IOException ex) {
			System.err.println(ex);
		} finally {//线程中关闭socket
			if(connection!=null){
				try {
					connection.close();
				} catch (IOException e) {
					// ignore;
				}
			}
		}

	}
		
	public static void main(String[] args) {
		int port=1313;
		int threadsNum=50;
		new DaytimeSocketServer(port, threadsNum).work();
	}
}
