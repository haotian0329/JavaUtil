package wanghao.util.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SocketServer {
	private int port;
	private int queueLength=0;
	private InetAddress bindAddress=null;
	private int threadsNum;
	private ExecutorService pool;
	private static final Logger auditLogger = Logger.getLogger("requests");
	private static final Logger errorLogger = Logger.getLogger("errors");
	
	public SocketServer(int port, int threadsNum) {
		this.port=port;
		this.threadsNum=threadsNum;
	}
	
	public SocketServer(int port, int threadsNum, int queueLength) {
		this(port,threadsNum);
		this.queueLength=queueLength;
	}
	
	public SocketServer(int port, int threadsNum, int queueLength, InetAddress bindAddress) {
		this(port,threadsNum,queueLength);
		this.bindAddress=bindAddress;
	}
		
	/**
	 * 由子类实现的抽象方法，与客户端具体的交互操作
	 * @param connection
	 */
	public abstract void operation(Socket connection, Logger auditLogger);

	/**
	 * 实际工作流程
	 */
	public void work() {
		pool = Executors.newFixedThreadPool(threadsNum);
		ServerSocket server=null;
		try {
			if(queueLength==0){
				server = new ServerSocket(port);
			}else if(bindAddress==null){
				server = new ServerSocket(port,queueLength);
			}else{
				server = new ServerSocket(port,queueLength,bindAddress);
			}			
			while (true) {
				try {//客户端socket在线程任务结束后再关闭
					Socket connection = server.accept();
					Runnable task = new PooledTaskServer(connection);
					pool.execute(task);
				} catch (IOException e) {
					errorLogger.log(Level.SEVERE,"accept error",e);
				}catch (RuntimeException e) {
					errorLogger.log(Level.SEVERE,"unexpected error "+e.getMessage(),e);
				}
			}
		} catch (IOException e) {
			errorLogger.log(Level.SEVERE,"Couldn't start server",e);
		}catch (RuntimeException e) {
			errorLogger.log(Level.SEVERE,"Couldn't start server: "+e.getMessage(),e);
		}finally{
			if(server!=null){
				try {
					server.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
	}

	/**
	 * 执行每个客户端连接的线程内部类
	 * @author wh
	 *
	 */
	private class PooledTaskServer implements Runnable{
		private Socket connection;
		public PooledTaskServer(Socket connection) {
			this.connection=connection;
		}		
		@Override
		public void run() {
			operation(connection,auditLogger);
		}
	}

}

